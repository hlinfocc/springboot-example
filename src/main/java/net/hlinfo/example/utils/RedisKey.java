package net.hlinfo.example.utils;

public interface RedisKey {
	/**
	 * 组织机构列表信息<br>
	 * key规则/说明: dp_orginfo_all_list:{pid}<br>
	 * value规则/说明: 组织机构列表信息
	 */
	String DpOrgInfoList = "dp_orginfo_all_list:";
	/**
	 * 登陆信息 key: login_{type}_{id} <br>
	 * key规则/说明: type=[ADMIN(管理员),MEM(普通用户)] <br>
	 * value规则/说明: logininfo序列化对象。不清除一直有效
	 */
	String login_type_id = "login_";
	/**
	 * 验证码 <br>
	 * key规则/说明:vrcode_{phone} phone=[手机号] <br>
	 * value规则/说明: 发送的验证码 配置文件里设过期时间
	 */
	String vrcode_phone = "vrcode_";
	/**
	 * 需要api权限校验的url <br>
	 * key规则/说明: perm_url_{type}_{id} type=[用户类型] id=[用户的id] <br>
	 * value规则/说明: [url] 数组;
	 */
	String perm_url_type_id = "perm_url_";
	
	String WeiXin_redirectUri = "WeiXin_redirectUri_";
	
	String exportExcelCode = "export.excel.code:";
}
