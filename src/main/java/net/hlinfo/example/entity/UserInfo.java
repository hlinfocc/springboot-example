package net.hlinfo.example.entity;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Table("user_info")
@ApiModel("管理员信息")
@Comment("管理员信息")
@TableIndexes({
	@Index(fields = {"account"},unique=true),
	@Index(fields= {"state"},unique=false)
})
public class UserInfo extends BaseEntity{
	private static final long serialVersionUID = 1L;
	/**
	* 登录账号
	*/
	@Column("account")
	@ColDefine(type=ColType.VARCHAR, width=50)
	@Comment(value="登录账号")
	@ApiModelProperty(value="登录账号")
	private String account;
	/**
	* 用户名
	*/
	@Column("username")
	@ColDefine(type=ColType.VARCHAR, width=50)
	@Comment(value="用户名")
	@ApiModelProperty(value="用户名")
	private String username;
	
	@Column("password")
	@ColDefine(notNull=false, type=ColType.TEXT)
	@Comment(value="密码")
	@ApiModelProperty(value="密码")
	@JsonProperty(access = Access.WRITE_ONLY) //密码不发送到前端，但是前段可以传过来
	private String password;
	
	@Column("email")
	@ColDefine(type=ColType.VARCHAR, width=50)
	@Comment(value="邮件")
	@ApiModelProperty(value="邮件")
	private String email;
	
	@Column("remark")
	@ColDefine(type=ColType.VARCHAR, width=500)
	@Comment(value="备注")
	@ApiModelProperty(value="备注")
	private String remark;
	
	@Column("state")
	@ColDefine(notNull=false,type=ColType.INT, width=3)
	@Comment(value="状态: 1 禁用 0 启用")
	@ApiModelProperty(value="状态: 1 禁用 0启用")
	@Default("0")
	private int state;
	
	@Column("user_level")
	@ColDefine(type=ColType.INT, width=3)
	@Comment(value="级别:0 超级管理员")
	@ApiModelProperty(value="级别:0 超级管理员")
	@Default("0")
	private int userLevel;
	
	
	@Column("last_login_time")
	@ColDefine(type=ColType.VARCHAR, width=25)
	@Comment(value="上一次登录时间")
	@ApiModelProperty(value="上一次登录时间")
	private String lastLoginTime;
	
	@Column("last_login_ip")
	@ColDefine(type=ColType.TEXT)
	@Comment(value="上一次登录ip")
	@ApiModelProperty(value="上一次登录ip")
	private String lastLoginIp;
	
	@Column("logintime")
	@ColDefine(type=ColType.VARCHAR, width=25)
	@Comment(value="这一次登录时间")
	@ApiModelProperty(value="这一次登录时间")
	private String logintime;
	
	@Column("loginip")
	@ColDefine(type=ColType.TEXT)
	@Comment(value="这一次登录ip")
	@ApiModelProperty(value="这一次登录ip")
	private String loginip;
	
	public int getUserLevel() {
		return userLevel;
	}
	public void setUserLevel(int userLevel) {
		this.userLevel = userLevel;
	}
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
	public String getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public String getLastLoginIp() {
		return lastLoginIp;
	}
	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}
	public String getLogintime() {
		return logintime;
	}
	public void setLogintime(String logintime) {
		this.logintime = logintime;
	}
	public String getLoginip() {
		return loginip;
	}
	public void setLoginip(String loginip) {
		this.loginip = loginip;
	}
}
