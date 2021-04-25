package net.hlinfo.example.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.hlinfo.example.entity.UserInfo;
import net.hlinfo.example.opt.LoginVo;
import net.hlinfo.example.opt.Logininfo;
import net.hlinfo.example.utils.Funs;
import net.hlinfo.example.utils.JwtUtil;
import net.hlinfo.example.utils.RedisKey;
import net.hlinfo.example.utils.RedisUtils;
import net.hlinfo.example.utils.Resp;

@Api(tags="登录")
@RequestMapping("/login")
@RestController
public class LoginController extends BaseController{
	@Autowired
	private Dao dao;
	@Value("${jwt.ttlday}")
	private long ttlday;
	@Value("${jwt.key}")
	private String jwtkey;
	
	@Autowired
	private RedisUtils redisUtils;
		
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation("管理员登录")
	@PostMapping("/admin")
	public Resp<Logininfo> adminLogin(@RequestBody LoginVo loginVo,HttpServletRequest request){
		if(Strings.isBlank(loginVo.getAccount()) || Strings.isBlank(loginVo.getPasswd())) {
			return new Resp<String>().error("用户名或者密码不能为空");
		}
		//ip限制
		/*
		String ip = Funs.getIpAddr(request);
		long ipexpire =  redisUtils.getExpire(ip);
		if((Duration.ofMinutes(5).getSeconds() - ipexpire)<30) {
			return new Resp<>().error("操作太频繁，请1分钟后再试");
		}
		*/
		UserInfo user = dao.fetch(UserInfo.class, Cnd.where("account", "=", loginVo.getAccount()));
		if(user == null) {
			return new Resp<String>().error("账户不存在^_^");
		}
		if(!Funs.passwdMatches(loginVo.getPasswd(), user.getPassword())) {
			return new Resp<String>().error("用户名或密码错误");
		}
		if(user.getState()==1) {
			return new Resp<String>().error("您的账号已被禁用^_^");
		}
		Logininfo<UserInfo> data = new Logininfo<UserInfo>();
		data.setData(user);
		data.setType(Logininfo.Type.ADMIN);
		data.setJwtToken(JwtUtil.createJWT(ttlday, NutMap.NEW()
			.addv("id", user.getId())
			.addv("type", Logininfo.Type.ADMIN)
			.addv("username", user.getUsername())
			.addv("userLevel", user.getUserLevel()), jwtkey));
		user.setLastLoginIp(user.getLoginip());
		user.setLastLoginTime(user.getLogintime());
		user.setLoginip(Funs.getIpAddr(request));
		user.setLogintime(Funs.getNowFullTime());
		user.setUpdatetime(new Date());
		dao.update(user);
		redisUtils.resetCacheData(RedisKey.login_type_id + "ADMIN_" + user.getId(), data, 24*60);
		return new Resp<Logininfo>().ok("登陆成功", data);
	}
	
}
