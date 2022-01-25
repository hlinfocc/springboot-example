package net.hlinfo.example.etc.security;

import java.util.Date;


import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import net.hlinfo.example.entity.UserInfo;
import net.hlinfo.example.etc.JwtConfig;
import net.hlinfo.opt.Func;

@Service
public class JwtUserService implements UserDetailsService {
	private JwtConfig jwt;
	
	private Dao dao;
	
	/*public JwtUserService() {
		this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();  //默认使用 bcrypt， strength=10 
	}*/

	public JwtUserService(JwtConfig jwt, Dao dao) {
		this.jwt = jwt;
		this.dao = dao;
	}
	public String getJwtKey(String username) {
		String salt = jwt.getJwtkey();
    	/**
    	 * @todo 从数据库或者缓存中取出jwt token生成时用的salt
    	 * salt = redisTemplate.opsForValue().get("token:"+username);
    	 */ 
		return salt;
	}
	public UserDetails getUserLoginInfo(String username) {
		UserInfo user = getUserInfoByDB(username,null);
    	return User.builder().username(user.getUsername())
    			.password(user.getPassword())
    			.roles(user.getRoles())
    			.build();
	}
	/**
	 * 从数据库或者缓存中加载用户信息
	 * @param username 用户名
	 * @param ip 用户IP，用户更新登录记录
	 * @return 用户信息
	 */
	public UserInfo getUserInfoByDB(String username,String ip) {
		UserInfo userInfo = dao.fetch(UserInfo.class, Cnd.where("isdelete","=",0).and("account","=",username));
		if(Func.isNotBlank(ip)) {
			userInfo.resetLoginInfo(ip, false);
			dao.updateIgnoreNull(userInfo);
		}
		return userInfo;
	}
	/**
	 * 生成jwt token
	 * @param user
	 * @return
	 */
	public String createJwtToken(UserDetails user) {
		String salt = jwt.getJwtkey(); //BCrypt.gensalt();  正式开发时可以调用该方法实时生成加密的salt
		/**
    	 * @todo 将salt保存到数据库或者缓存中
    	 * redisTemplate.opsForValue().set("token:"+username, salt, 3600, TimeUnit.SECONDS);
    	 */
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		long expMillis = nowMillis + jwt.getTtlday() * 24 * 60 * 60 * 1000;
		Date expDate = new Date(expMillis);
		// 指定签名的时候使用的签名算法，并填入密钥。
		Algorithm algorithm = Algorithm.HMAC512(salt);
        return JWT.create()
        		 .withSubject(user.getUsername())
               .withExpiresAt(expDate)
               .withIssuedAt(now)
               .sign(algorithm);
	}
		
	public void deleteUserLoginInfo(String username) {
		/**
		 * @todo 清除数据库或者缓存中登录salt
		 */
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws AuthenticationException {
		//密码规则：前端对密码做sm3加密,后端再用passwordEncoder.encode
		UserInfo userInfo= dao.fetch(UserInfo.class, Cnd.where("isdelete","=",0).and("account","=",username));
		if(userInfo==null) {
			throw new BadCredentialsException("用户名不存在");
		}
	    return User.builder().username(username)
	    		.password(userInfo.getPassword())
	    		.roles(userInfo.getRoles())
	    		.disabled(userInfo.getStstus()==1?true:false)
	    		.build();
	}
	
}
