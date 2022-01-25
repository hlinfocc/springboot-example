package net.hlinfo.example.etc.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.header.Header;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.hlinfo.example.utils.Resp;


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private JwtUserService jwtUserService;
    @Override
    public void configure(WebSecurity web) throws Exception {
    	//定义url白名单
    	String[] whitelist = {"/v3/api-docs"
    			,"/v2/api-docs"
    			,"/swagger-resources"
    			,"/webjars/**"
    			,"/doc.html"
    			,"/sm3"
    			,"/login.php"
    			};
        web.ignoring().antMatchers(whitelist);
	}
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	auth.authenticationProvider(daoAuthenticationProvider()).authenticationProvider(jwtAuthenticationProvider());
	}
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		        .antMatchers("/image/**").permitAll()
		        .antMatchers("/admin/**").hasAnyRole("ADMIN")
		        .antMatchers("/article/**").hasRole("USER")
		        .anyRequest().authenticated()
		        .and()
		     // 由于使用的是JWT不使用session，这里禁用csrf
		    .csrf().disable()
		    //禁用form登录
		    .formLogin().disable()
		    // 基于token，所以不需要session，禁用
		    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		    .and()
		    //启用跨域请求
		    .cors()
		    .and()
		    .headers().addHeaderWriter(new StaticHeadersWriter(Arrays.asList(
		    		new Header("Access-control-Allow-Origin","*"),
		    		new Header("Access-Control-Expose-Headers","token"))))
		    .and()
		    .addFilterAfter(new OptionsRequestFilter(), CorsFilter.class)
		    .apply(new JsonLoginConfigurer<>()).loginSuccessHandler(jsonLoginSuccessHandler())
		    .and()
		    .apply(new JwtLoginConfigurer<>()).tokenValidSuccessHandler(jwtRefreshSuccessHandler()).permissiveRequestUrls("/logout")
		    .and()
		    .logout()
//		    .logoutUrl("/logout")   //默认就是"/logout"
	        .addLogoutHandler(tokenClearLogoutHandler())
	        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
		    .and()
		    .exceptionHandling()
		    .authenticationEntryPoint(new AuthenticationEntryPoint() {
		        @Override
		        public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException authException) throws IOException, ServletException {
		            resp.setContentType("application/json;charset=utf-8");
		            PrintWriter out = resp.getWriter();
		            Resp respBean = new Resp().error("访问失败!");
		            authException.getLocalizedMessage();
		            authException.printStackTrace();
		            if (authException instanceof InsufficientAuthenticationException) {
		            		respBean.setCode(403);
		            		respBean.setMsg("访问受限，该资源需要身份验证");
		              }
		            out.write(new ObjectMapper().writeValueAsString(respBean));
		            out.flush();
		            out.close();
		        	}
	            });
		    
	}
   
   @Bean
   public PasswordEncoder passwordEncoder() {
//    	return NoOpPasswordEncoder.getInstance();
    	return new BCryptPasswordEncoder();
    }
    
   @Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
	    return super.authenticationManagerBean();
	}
	
	@Bean("jwtAuthenticationProvider")
	protected AuthenticationProvider jwtAuthenticationProvider() {
		return new JwtAuthenticationProvider(jwtUserService);
	}
	
	@Bean("daoAuthenticationProvider")
	protected AuthenticationProvider daoAuthenticationProvider() throws Exception{
		//这里会默认使用BCryptPasswordEncoder比对加密后的密码，注意要跟createUser时保持一致
		DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
		daoProvider.setUserDetailsService(userDetailsService());
		daoProvider.setPasswordEncoder(passwordEncoder());
		return daoProvider;
	}

	@Override
	protected UserDetailsService userDetailsService() {
		return  jwtUserService;//new JwtUserService();
	}
	
	/*@Bean("jwtUserService")
	protected JwtUserService jwtUserService() {
		return new JwtUserService();
	}*/
	
	@Bean
	protected JsonLoginSuccessHandler jsonLoginSuccessHandler() {
		return new JsonLoginSuccessHandler(jwtUserService);
	}
	
	@Bean
	protected JwtRefreshSuccessHandler jwtRefreshSuccessHandler() {
		return new JwtRefreshSuccessHandler(jwtUserService);
	}
	
	@Bean
	protected TokenClearLogoutHandler tokenClearLogoutHandler() {
		return new TokenClearLogoutHandler(jwtUserService);
	}
	
	@Bean
	protected CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET","POST","HEAD", "OPTION","DELETE","PUT"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.addExposedHeader("token");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
