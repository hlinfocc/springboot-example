package net.hlinfo.example.opt;

import io.swagger.annotations.ApiModelProperty;

public class LoginVo {
	@ApiModelProperty("账号")
	private String account;
	@ApiModelProperty("密码，前端请使用sm3加密")
	private String passwd;
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
}
