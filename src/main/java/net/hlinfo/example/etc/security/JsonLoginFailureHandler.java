package net.hlinfo.example.etc.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.hlinfo.example.utils.Resp;


public class JsonLoginFailureHandler implements AuthenticationFailureHandler {
	

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
			// TODO Auto-generated method stub
		 response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        String msg = "登录失败！";
        if (exception instanceof LockedException) {
      	 	msg = "账户被锁定，登录失败！";
        } else if (exception instanceof BadCredentialsException) {
      	  	msg = "账户或者密码错误，登录失败！";
        } else if (exception instanceof DisabledException) {
      	  	msg = "账户被禁用，登录失败！";
        } else if (exception instanceof AccountExpiredException) {
      	  	msg = "账户已过期，登录失败！";
        } else if (exception instanceof CredentialsExpiredException) {
      	  	msg = "密码已过期，登录失败！";
        } else {
        	msg = isContainChinses(exception.getMessage())?exception.getMessage():"登录失败！";
         }
        Resp<String> respBean = new Resp<String>().error(msg);
        respBean.setCode(403);
        respBean.setData(exception.getMessage());
        out.write(new ObjectMapper().writeValueAsString(respBean));
        out.flush();
        out.close();
	}
	
	private static boolean isContainChinses(CharSequence str) {
		if (null == str || str.length() == 0) {return false;}
		Pattern pattern = Pattern.compile("[\u4E00-\u9FFF]");
       Matcher matcher = pattern.matcher(str);
		if(matcher.find()) {
			return true;
		}else {
			return false;
		}
	}
}
