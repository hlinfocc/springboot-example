package net.hlinfo.example.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.UUID;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.hlinfo.example.etc.JwtConfig;
import net.hlinfo.example.etc.WXLoginConfig;
import net.hlinfo.example.utils.Funs;
import net.hlinfo.example.utils.JwtUtil;
import net.hlinfo.example.utils.RedisKey;
import net.hlinfo.example.utils.RedisUtils;
import net.hlinfo.example.utils.Resp;

@Api(tags = "微信网页授权登陆模块")
@RestController
@RequestMapping("/weixin_login")
public class WeixinLoginController extends BaseController {
	@Autowired
	private Dao dao;
	
	private JwtConfig jwt;
	
	@Autowired
	private RedisUtils redisUtils;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private WXLoginConfig WX;


	@ApiOperation(value="调起微信网页授权",notes="")
	@GetMapping("/authorize")
	public Resp<String> authorize(@ApiParam(value="前端回调地址，用于在微信授权登陆成功后重定向到该地址",required = true) @RequestParam String redirectUri){
		try {
			String wxRedirectUri = WX.redirectUri+"/mem/weixin_login/back";
			wxRedirectUri = URLEncoder.encode(wxRedirectUri,"utf-8");
			
			String state = Funs.getId();
			
			String toWxUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
			toWxUrl = toWxUrl.replace("APPID", WX.getAppId());
			toWxUrl = toWxUrl.replace("REDIRECT_URI", wxRedirectUri);
			toWxUrl = toWxUrl.replace("STATE", state);
			
			redisUtils.setCacheData(RedisKey.WeiXin_redirectUri+state, redirectUri,5);
			return new Resp<String>().ok("成功", toWxUrl);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			log.error(e.getMessage());
		}
		return new Resp<String>().error("创建微信授权登陆地址失败");
	}
	
	@ApiOperation("授权回调地址")
	@RequestMapping("/back")
	public String weiXinBack(@RequestParam String code,@RequestParam String state) {
		String redirectUri = redisUtils.getCacheObject(RedisKey.WeiXin_redirectUri+state);
		redirectUri = Funs.isBlank(redirectUri)?"#":redirectUri;
		if(redirectUri.indexOf("?")==-1) {
			redirectUri += "?";
		}
		String redirectUriNone = "openid=&headimgurl=&nickname=";
		
		if(Funs.isBlank(code)) {
			return "<script>alert(\"授权登陆失败，请重试\");location='"+redirectUri+redirectUriNone+"';</script>";
		}
		String msg = "";
		String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+WX.getAppId()+"&secret="+WX.getAppSecret()+"&code="+code+"&grant_type=authorization_code";
		Response rsak = Http.get(url);
		JSONObject jsonak= JSON.parseObject(rsak.getContent());
		if(jsonak.containsKey("access_token")) {
			String access_token=jsonak.getString("access_token");
			String openid=jsonak.getString("openid");
			String getuserurl="https://api.weixin.qq.com/sns/userinfo?access_token="+access_token+"&openid="+openid+"&lang=zh_CN";
			Response rsUserInfo= Http.get(getuserurl);
			JSONObject jsonui= JSON.parseObject(rsUserInfo.getContent());
			if(jsonui.containsKey("openid")) {
				//获取用户信息成功
				String nickname = jsonui.getString("nickname")==null?"":jsonui.getString("nickname");
				nickname = Funs.filterEmoji(nickname);
				String headimgurl = jsonui.getString("headimgurl")==null?"":jsonui.getString("headimgurl");
				//插入用户信息
				
				redirectUri += "openid="+openid+"&headimgurl="+headimgurl+"&nickname="+nickname;
				return "<script>location='"+redirectUri+"';</script>";
			}else if(jsonui.containsKey("errcode")) {
				//获取用户信息失败
				msg = "获取用户信息失败，错误代码："+jsonui.getString("errcode")+"，原因："+jsonui.getString("errmsg");
				return "<script>alert(\""+msg+"\");location='"+redirectUri+redirectUriNone+"';</script>";
			}else {
				return "<script>alert(\"获取用户信息失败，请重试\");location='"+redirectUri+redirectUriNone+"';</script>";
			}
		}else if(jsonak.containsKey("errcode")) {
			//获取access_token失败
			msg = "获取access_token失败，错误代码："+jsonak.getString("errcode")+"，原因："+jsonak.getString("errmsg");
			return "<script>alert(\""+msg+"\");location='"+redirectUri+redirectUriNone+"';</script>";
		}else {
			return "<script>alert(\"获取access_token失败，请重试\");location='"+redirectUri+redirectUriNone+"';</script>";
		}
	}
	
	/**
	 * 微信小程序登录
	 * @param request
	 * @param model
	 * @return
	 */
	@ApiOperation("微信小程序登录")
	@GetMapping("/miniProgramLogin")
	public Resp<String> miniProgramLogin(@ApiParam(value="登录凭证（code）",required = true) @RequestParam(defaultValue = "",required = false) String code
			,@ApiParam(value="头像",required = false) @RequestParam(defaultValue = "",required = false) String headimgurl
			,@ApiParam(value="昵称",required = false) @RequestParam(defaultValue = "",required = false) String nickname){
		if(code==null || "".equals(code)) {
			return new Resp<String>().error("Authentication Failure！code is null");
		}
		
		String url = "https://api.weixin.qq.com/sns/jscode2session"
				+ "?appid="+WX.getMiniAppId()+"&secret="+WX.getMiniAppSecret()+"&js_code="+code+"&grant_type=authorization_code";
		String rs= HttpUtil.get(url);
		log.debug("jscode2session result:"+rs);
		JSONObject json=JSON.parseObject(rs);
		if(json.containsKey("errcode") && !"0".equals(json.getString("errcode"))) {
			return new Resp<String>().error(json.getString("errmsg")).data(json);
    	}
		String openid = json.getString("openid");
		String session_key = json.getString("session_key");
		String unionid = json.containsKey("unionid")?json.getString("unionid"):"";
		
		//openid 等信息存库
		

		String token = JwtUtil.createJWT(jwt.getTtlday(), NutMap.NEW()
				.addv("id", openid)
				.addv("type", "")
				.addv("username", ""), jwt.getJwtkey());
		
		return new Resp<String>().ok("登陆成功").data(token);
	}
	
	
}
