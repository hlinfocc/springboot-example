package net.hlinfo.example.entity;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.Table;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("导航菜单信息")
@Comment("导航菜单信息")
@Table("nav_menu")
public class NavMenu extends BaseEntity{
	private static final long serialVersionUID = 1L;
		
	@Column(value ="pid")
	@ColDefine(type=ColType.VARCHAR,width = 36)
	@Comment(value="父级ID")
	@ApiModelProperty(value="父级ID")
	private String pid;
	
	@Column("nav_name")
	@ColDefine(type=ColType.TEXT)
	@Comment(value="菜单名称")
	@ApiModelProperty(value="菜单名称")
	private String navName;
	
	@Column(value="nav_img_url")
	@ColDefine(type=ColType.TEXT)
	@Comment(value="标题图")
	@ApiModelProperty(value="标题图")
	private String navImgUrl;
	
	@Column(value="sort")
	@ColDefine(type=ColType.INT,customType = "integer")
	@Comment(value="排序")
	@ApiModelProperty(value="排序")
	@Default("0")
	private int sort;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getNavName() {
		return navName;
	}

	public void setNavName(String navName) {
		this.navName = navName;
	}

	public String getNavImgUrl() {
		return navImgUrl;
	}

	public void setNavImgUrl(String navImgUrl) {
		this.navImgUrl = navImgUrl;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}
	
}
