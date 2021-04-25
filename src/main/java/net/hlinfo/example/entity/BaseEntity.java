package net.hlinfo.example.entity;

import java.io.Serializable;
import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.json.Json;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;

public class BaseEntity implements Serializable{

	private static final long serialVersionUID = -3220554378544217428L;

	@Name
	@Column("id")
	@ColDefine(notNull=false, type=ColType.VARCHAR, width=32)
	@Comment(value="主键ID")
	@ApiModelProperty("权限主键,可根据需要传")
	private String id;
	
	/**
	* 创建时间
	*/
	@Column("createtime")
	@ColDefine(notNull=true, type=ColType.DATETIME, width=25)
	@Comment(value="创建时间")
	@ApiModelProperty(hidden = true)
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createtime;
	/**
	* 上一次更新时间
	*/
	@Column("updatetime")
	@ColDefine(notNull=true, type=ColType.DATETIME, width=25)
	@Comment(value="最后更新时间")
	@ApiModelProperty(hidden = true)
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updatetime;
	

	/**
	* 是否删除
	*/
	@Column("isdelete")
	@ColDefine(notNull=true, type=ColType.INT, width=2,customType = "integer")
	@Comment(value="是否删除： 0没有删除 1 删除")
	@ApiModelProperty(hidden = true)
	@Default("0")
	private int isdelete;
	
	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}

	

	public Date getCreatetime() {
		return createtime;
	}



	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}



	public Date getUpdatetime() {
		return updatetime;
	}



	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}



	public int getIsdelete() {
		return isdelete;
	}



	public void setIsdelete(int isdelete) {
		this.isdelete = isdelete;
	}



	public String toString() {
		return Json.toJson(this);
	}
}
