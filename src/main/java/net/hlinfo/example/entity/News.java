package net.hlinfo.example.entity;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.Table;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("新闻信息")
@Comment("新闻信息")
@Table("news")
public class News extends BaseEntity{
	private static final long serialVersionUID = 1L;
		
	@Column("news_title")
	@ColDefine(type=ColType.TEXT)
	@Comment(value="标题")
	@ApiModelProperty(value="标题")
	@NotEmpty(message = "标题不能为空")
	private String newsTitle;
	
	@Column(value ="news_auther")
	@ColDefine(type=ColType.VARCHAR,width = 100)
	@Comment(value="作者")
	@ApiModelProperty(value="作者")
	private String newsAuther;
	
	@Column(value="news_origin")
	@ColDefine(type=ColType.TEXT)
	@Comment(value="来源")
	@ApiModelProperty(value="来源")
	private String newsOrigin;
	
	@Column(value="menu_id")
	@ColDefine(type=ColType.VARCHAR,width = 36)
	@Comment(value="分类ID")
	@ApiModelProperty(value="分类ID")
	@NotEmpty(message = "分类ID不能为空")
	private String menuId;
	
	@Column(value="menu_name")
	@ColDefine(type=ColType.TEXT)
	@Comment(value="分类")
	@ApiModelProperty(value="分类")
	@NotEmpty(message = "分类不能为空")
	private String menuName;
	
	@Column(value="summary")
	@ColDefine(type=ColType.VARCHAR,width = 200)
	@Comment(value="摘要(200个字符以内)")
	@ApiModelProperty(value="摘要(200个字符以内)")
	@NotEmpty(message = "摘要不能为空")
	@Size(min = 10,max = 200)
	private String summary;
	
	@Column(value="titleimg")
	@ColDefine(type=ColType.TEXT)
	@Comment(value="标题图")
	@ApiModelProperty(value="标题图")
	private String titleimg;
	
	/*@Column(name="img_url_list")
	@ApiModelProperty(value="内容中的图片地址，个多用英文逗号隔开")
	private JSONArray imgUrlList;*/
	
	@Column(value="content")
	@ColDefine(type=ColType.TEXT)
	@Comment(value="内容")
	@ApiModelProperty(value="内容")
	@NotEmpty(message = "内容不能为空")
	private String content;
	
	@Column(value="push_date")
	@ColDefine(type=ColType.VARCHAR,width = 25)
	@Comment(value="发布日期(格式为:yyyy-mm-dd)")
	@ApiModelProperty(value="发布日期(格式为:yyyy-mm-dd)")
	@NotEmpty(message = "发布日期不能为空")
	private String pushDate;

	@Column(value="status")
	@ColDefine(type=ColType.INT,customType = "integer")
	@Comment(value="状态：0草稿，1发布，2撤稿")
	@ApiModelProperty(value="状态：0草稿，1发布，2撤稿")
	@Default("0")
	@Min(value = 0)
	@Max(value = 2)
	private int status;
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTitleimg() {
		return titleimg;
	}

	public void setTitleimg(String titleimg) {
		this.titleimg = titleimg;
	}

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
