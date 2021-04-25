package net.hlinfo.example.etc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="weixin",ignoreInvalidFields=true, ignoreUnknownFields=true)
public class WXLoginConfig {
	/**
	 * 微信公众号APPID
	 */
	public String appId;
	/**
	 * 微信公众号APP密钥
	 */
	public String appSecret;
	
	/**
	 * 回调地址前缀,结尾不带/，如：https：//jyzdapi.dev.htedu.cc
	 */
	public String redirectUri;
	
	/**
	 * 微信小程序APPID
	 */
	public String miniAppId;
	
	/**
	 * 微信小程序APP密钥
	 */
	public String miniAppSecret;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getMiniAppId() {
		return miniAppId;
	}

	public void setMiniAppId(String miniAppId) {
		this.miniAppId = miniAppId;
	}

	public String getMiniAppSecret() {
		return miniAppSecret;
	}

	public void setMiniAppSecret(String miniAppSecret) {
		this.miniAppSecret = miniAppSecret;
	}
	
}
