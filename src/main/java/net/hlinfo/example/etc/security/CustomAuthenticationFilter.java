package net.hlinfo.example.etc.security;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;



/**
 * 自定义Filter，重写UsernamePasswordAnthenticationFilter
 * @author hadoop
 *
 */
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	  @Override
	  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
	 
	    //当Content-Type为json时尝试身份验证
	    if(request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)){
	 
	      //使用 jackson 反序列化json
	      ObjectMapper mapper = new ObjectMapper();
	      UsernamePasswordAuthenticationToken authRequest = null;
	      try (InputStream is = request.getInputStream()){
	    	  AuthenticationVo authenticationBean = mapper.readValue(is,AuthenticationVo.class);
	        authRequest = new UsernamePasswordAuthenticationToken(
	            authenticationBean.getUsername(), authenticationBean.getPassword());
	      }catch (IOException e) {
	        e.printStackTrace();
	        new UsernamePasswordAuthenticationToken(
	            "", "");
	      }finally {
	        setDetails(request, authRequest);
	        return this.getAuthenticationManager().authenticate(authRequest);
	      }
	    }else {
	    	throw new ParamterErrorException("参数错误,Content-Type需为"+MediaType.APPLICATION_JSON_VALUE);
//	      return super.attemptAuthentication(request, response);
	    }
	  }
}
