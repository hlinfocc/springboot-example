package net.hlinfo.example.etc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig{
	
	
    @Bean
    public CorsFilter corsFilter(){
        //配置初始化对象
    	CorsConfiguration config = new CorsConfiguration();
        //允许跨域的域名，如果要携带cookie，不能写* 。  *：代表所有的域名都可以访问
    	config.setAllowCredentials(true);
    	config.addAllowedOrigin("*");
    	config.addAllowedHeader("*"); //允许携带任何头信息
    	config.addAllowedMethod("*");//代表所有的请求方法
       config.setMaxAge(18000L);// 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
        
          //初始化cors配置源对象
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        //返回corsFilter实例，参数:cors配置源对象
        return new CorsFilter(source);
    }
}
