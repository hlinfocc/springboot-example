package net.hlinfo.example.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("新闻信息")
@Entity
@Table(name = "news")
@org.hibernate.annotations.Table(appliesTo = "news",comment="新闻信息")
public class News implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id //这是一个主键
	@GeneratedValue(strategy = GenerationType.IDENTITY)//自增主键
	@org.nutz.dao.entity.annotation.Id
	@Column(length = 255,columnDefinition="comment '标题'")
	@ApiModelProperty("主键")
	private Integer id;
	
	@Column(name="news_title",length = 255,columnDefinition="comment '标题'")
	@ApiModelProperty(value="标题")
	private String newsTitle;
	
	@Column(name="news_auther",length = 100,columnDefinition="comment '作者'")
	@ApiModelProperty(value="作者")
	private String newsAuther;
	
	@Column(name="news_origin")
	@ApiModelProperty(value="来源")
	private String newsOrigin;
	
	@Column(name="menu_id",length = 32,columnDefinition="comment '分类ID'")
	@ApiModelProperty(value="分类ID")
	private String menuId;
	
	@Column(name="menu_name",length = 255,columnDefinition="comment '分类'")
	@ApiModelProperty(value="分类")
	private String menuName;
	
	@Column(name="summary",length = 200,columnDefinition="comment '摘要(200个字符以内)'")
	@ApiModelProperty(value="摘要(200个字符以内)")
	private String summary;
	
	/*@Column(name="img_url_list")
	@ApiModelProperty(value="内容中的图片地址，个多用英文逗号隔开")
	private JSONArray imgUrlList;*/
	
	@Column(name="content",length = 200,columnDefinition="text null comment '内容'")
	@ApiModelProperty(value="内容")
	private String content;
	
	@Column(name="push_date",length = 200,columnDefinition="comment '发布日期(格式为:yyyy-mm-dd)'")
	@ApiModelProperty(value="发布日期(格式为:yyyy-mm-dd)")
	private String pushDate;

	
	public String getNewsTitle() {
		return newsTitle;
	}

	public void setNewsTitle(String newsTitle) {
		this.newsTitle = newsTitle;
	}

	public String getNewsAuther() {
		return newsAuther;
	}

	public void setNewsAuther(String newsAuther) {
		this.newsAuther = newsAuther;
	}

	public String getNewsOrigin() {
		return newsOrigin;
	}

	public void setNewsOrigin(String newsOrigin) {
		this.newsOrigin = newsOrigin;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPushDate() {
		return pushDate;
	}

	public void setPushDate(String pushDate) {
		this.pushDate = pushDate;
	}

}
