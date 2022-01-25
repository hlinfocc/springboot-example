package net.hlinfo.example.etc.security;

import java.util.Calendar;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.NonceExpiredException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import net.hlinfo.opt.Func;

public class JwtAuthenticationProvider implements AuthenticationProvider{
	
	private JwtUserService userService;
	
	public JwtAuthenticationProvider(JwtUserService userService) {
		this.userService = userService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		DecodedJWT jwt = ((JwtAuthenticationToken)authentication).getToken();
		if(jwt.getExpiresAt().before(Calendar.getInstance().getTime()))
			throw new NonceExpiredException("Token过期");
		String username = jwt.getSubject();
		UserDetails user = userService.getUserLoginInfo(username);
		String jwtKey = userService.getJwtKey(username);
		if(user == null || Func.isBlank(jwtKey)) {
			throw new NonceExpiredException("Token过期");
		}
		try {
            Algorithm algorithm = Algorithm.HMAC512(jwtKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withSubject(username)
                    .build();
            verifier.verify(jwt.getToken());
        } catch (Exception e) {
            throw new JwtTokenErrorException("token校验失败", e);
        }
		JwtAuthenticationToken token = new JwtAuthenticationToken(user, jwt, user.getAuthorities());
		return token;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.isAssignableFrom(JwtAuthenticationToken.class);
	}

}
