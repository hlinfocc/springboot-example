package net.hlinfo.example.entity;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;


@Table("oplogs")
@Comment("操作日志")
public class Oplogs extends BaseEntity{
	private static final long serialVersionUID = 1L;

	@Column("account")
	@ColDefine(notNull=false, type=ColType.TEXT)
	@Comment(value="操作账号")
	private String account;
	
	@Column("mokuai")
	@ColDefine(notNull=false, type=ColType.TEXT)
	@Comment(value="操作模块")
	private String mokuai;
	
	@Column("pager")
	@ColDefine(notNull=false, type=ColType.TEXT)
	@Comment(value="操作页面")
	private String pager;
	
	@Column("gongneng")
	@ColDefine(notNull=false, type=ColType.TEXT)
	@Comment(value="操作功能")
	private String gongneng;
	
	@Column("logcontent")
	@ColDefine(notNull=false, type=ColType.TEXT)
	@Comment(value="日至内容")
	private String logcontent;
	
	@Column("remark")
	@ColDefine(notNull=false, type=ColType.TEXT)
	@Comment(value="备注")
	private String remark;
	
	@Column("ip")
	@ColDefine(notNull=false, type=ColType.VARCHAR,width=50)
	@Comment(value="ip")
	private String ip;
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getMokuai() {
		return mokuai;
	}
	public void setMokuai(String mokuai) {
		this.mokuai = mokuai;
	}
	public String getPager() {
		return pager;
	}
	public void setPager(String pager) {
		this.pager = pager;
	}
	public String getGongneng() {
		return gongneng;
	}
	public void setGongneng(String gongneng) {
		this.gongneng = gongneng;
	}
	public String getLogcontent() {
		return logcontent;
	}
	public void setLogcontent(String logcontent) {
		this.logcontent = logcontent;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
