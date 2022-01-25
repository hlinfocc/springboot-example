package net.hlinfo.example.service;


import javax.servlet.http.HttpServletRequest;

import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.hlinfo.example.entity.Oplogs;
import net.hlinfo.opt.Func;


@Service
public class OpLogsService {
	@Autowired
	private Dao dao;
	
	/**
	 * 操作日志
	 * @param account
	 * @param mokuai
	 * @param pager
	 * @param gongneng
	 * @param logcontent
	 * @param remark
	 * @param request
	 */
	public void addOpLogs(String account,String mokuai,String pager,String gongneng,String logcontent,String remark,HttpServletRequest request) {
		Oplogs logs = new Oplogs();
		logs.init();
		logs.setAccount(account);
		logs.setMokuai(mokuai);
		logs.setPager(pager);
		logs.setGongneng(gongneng);
		logs.setLogcontent(logcontent);
		logs.setRemark(remark);
		logs.setIp(Func.getIpAddr(request));
		dao.insert(logs);
	}
	/**
	 * admin账号操作日志
	 * @param gongneng
	 * @param logcontent
	 * @param request
	 */
	public void AdminAddOpLogs(String gongneng,String logcontent,HttpServletRequest request) {
		Oplogs logs = new Oplogs();
		logs.init();
		logs.setAccount("admin");
		logs.setMokuai("后台管理");
		logs.setPager("");
		logs.setGongneng(gongneng);
		logs.setLogcontent(logcontent);
		logs.setRemark("");
		logs.setIp(Func.getIpAddr(request));
		dao.insert(logs);
	}
	public void AdminAddOpLogs(String gongneng,String logcontent,String account,HttpServletRequest request) {
		Oplogs logs = new Oplogs();
		logs.init();
		logs.setAccount(account);
		logs.setMokuai("后台管理");
		logs.setPager("");
		logs.setGongneng(gongneng);
		logs.setLogcontent(logcontent);
		logs.setRemark("");
		logs.setIp(Func.getIpAddr(request));
		dao.insert(logs);
	}
	
}
