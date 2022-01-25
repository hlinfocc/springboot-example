package net.hlinfo.example.etc.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.dao.Cnd;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.hlinfo.example.entity.UserInfo;
import net.hlinfo.example.utils.Resp;
import net.hlinfo.opt.Func;
import net.hlinfo.opt.Jackson;


public class JsonLoginSuccessHandler implements AuthenticationSuccessHandler {
	
	private JwtUserService jwtUserService;
    
    public JsonLoginSuccessHandler(JwtUserService jwtUserService) {
        this.jwtUserService = jwtUserService;
    }
	
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
          //生成token
        String token = jwtUserService.createJwtToken((UserDetails)authentication.getPrincipal());
        response.setHeader("token", token);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("authentication", authentication.getPrincipal());
        JsonNode json = Jackson.toJsonObject(authentication.getPrincipal());
        String userName = json.get("username").asText();
          //查询用户信息
        map.put("userInfo", jwtUserService.getUserInfoByDB(userName,Func.getIpAddr(request)));
        map.put("token", token);
        PrintWriter out = response.getWriter();
        Resp respBean = new Resp().ok("授权成功",map);
        out.write(Jackson.toJSONString(respBean));
        out.flush();
        out.close();
    }

}
