package net.hlinfo.example.opt;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("登陆信息")
public class Logininfo<T> implements Serializable{
	
	public static final class Type {
		public static final int NONE = 0;
		public static final int ADMIN = 1;
		public static final int MEM = 2;
		public static final int CUSTOM = 3;
	}
	
	@ApiModelProperty("类型 1管理员登陆信息  2会员登陆信息")
	private int type;
	
	@ApiModelProperty("生成的jwttoken口令")
	private String jwtToken;
	
	@ApiModelProperty("数据类型[普通用户登陆，那就是member信息，管理员登陆那就是userinfo]")
	private T data;

	public Logininfo() {}
	
	public Logininfo(int type, String jwtToken, T data) {
		this.type = type;
		this.jwtToken = jwtToken;
		this.data = data;
	}

	public int getType() {
		return type;
	}

	public String getJwtToken() {
		return jwtToken;
	}

	public T getData() {
		return data;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}

	public void setData(T data) {
		this.data = data;
	}
}
