package net.hlinfo.example.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import net.hlinfo.example.etc.JwtConfig;
import net.hlinfo.example.opt.Logininfo;
import net.hlinfo.example.utils.Funs;
import net.hlinfo.example.utils.JwtUtil;
import net.hlinfo.example.utils.Resp;

@WebFilter(filterName = "authFilter",urlPatterns = {"/*"})
@Component
public class AuthFilter implements Filter {
	@Autowired
	private JwtConfig jwt;
	
	@Value("${spring.profiles.active}")
	private String profiles;
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest request = (HttpServletRequest) servletRequest;
       HttpServletResponse response = (HttpServletResponse) servletResponse;
       String token = request.getHeader("token");
       response.setHeader("Access-Control-Allow-Origin", "*");
       response.setHeader("Access-Control-Allow-Headers", "*");
       response.setHeader("Access-Control-Allow-Methods", "*");
       
       String path = request.getRequestURI();
       if(Funs.equals(profiles, "dev") || path.startsWith("/login") || path.endsWith("/")
    		   || path.startsWith("/doc.html") || path.startsWith("/webjars")
    		   || path.startsWith("/swagger-resources")
    		   || path.startsWith("/v2/api-docs")
    		   || path.startsWith("/upload/download")
    		   || path.endsWith("/sm3")) {
    		  filterChain.doFilter(request, response);
        }else {
	       if(Strings.isNotBlank(token) && !"undefined".equals(token) && !"null".equals(token)) {
				try {
					Claims claims = JwtUtil.parseJWT(token, jwt.getJwtkey());
					int userLevel = Funs.string2int(claims.get("userLevel"));
					int utype = Funs.string2int(claims.get("type"));
					String orgid = (String)claims.get("orgid");
					if(userLevel>0) {
						// 后台支部管理员注入机构支部ID参数
						request = this.handleAdmin(orgid, utype,path, claims, request);
					}
					filterChain.doFilter(request, response);
				}catch(ExpiredJwtException e) {
					System.out.println(e);
					this.echo(response,402, "登录过期");
					//return false;
				}catch(Exception e) {
					System.out.println(e);
					this.echo(response,402, "token失效");
					//return false;
				}
			}else {
				this.echo(response,403, "鉴权失败，token不能为空");
				//return false;
			}
        }
	}
	private HttpServletRequest handleAdmin(String orgid,int utype,String path,Claims claims, HttpServletRequest request) throws IOException {
		if(utype == Logininfo.Type.ADMIN) {
			if(Strings.equals("get", request.getMethod().toLowerCase())) {
				// 如果是学院管理员不管传啥部门id，都只能操作自己部门的数据
				// ^ v ^有特殊处理则在具体的方法里处理吧
		       Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
		       parameterMap.put("orgid", new String[]{orgid});
				request = new ParameterServletRequestWrapper(request, parameterMap);
			}else {
				// 其他方法中添加参数日后再说
				// ^ v ^ 一般查询都是get, 这里可以不用处理
			}
		}
		return request;
	}
	private void echo(HttpServletResponse resp,int code, String msg) {
    	resp.setCharacterEncoding("utf-8");
    	resp.setContentType("application/json; charset=utf-8");
    	try {
	    	PrintWriter writer = resp.getWriter();
	    	Resp res = new Resp().error(code,msg);
	    	writer.write(Json.toJson(res));
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
	
}
