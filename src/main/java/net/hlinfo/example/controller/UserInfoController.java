package net.hlinfo.example.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.hlinfo.example.entity.UserInfo;
import net.hlinfo.example.mybatis.service.MybatisService;
import net.hlinfo.example.utils.Funs;
import net.hlinfo.example.utils.QueryResult;
import net.hlinfo.example.utils.Resp;

@Api(tags = "管理员模块")
@RestController
@RequestMapping("/user")
public class UserInfoController extends BaseController {
	
	@Autowired
	private Dao dao;

	@Autowired
	private MybatisService MS;
	
	@SuppressWarnings("unchecked")
	@ApiOperation(value="添加/编辑管理员")
	@PostMapping("/addOrUpdate")
	public Resp<UserInfo> addOrUpdate(@RequestBody UserInfo userInfo){
		if(userInfo == null) {
			return new Resp().error("参数不能空");
		}
		Cnd cndCount = Cnd.where("isdelete", "=", 0)
				.and("account", "=", userInfo.getAccount());
		if(Strings.isNotBlank(userInfo.getId())) {
			cndCount.and("id", "!=", userInfo.getId());
		}
		int count = dao.count(UserInfo.class, cndCount);
		if(count > 0) {
			return new Resp().error("[" + userInfo.getAccount() + "]此账号已存在,请换一个账号");
		}
		
		if(Strings.isNotBlank(userInfo.getPassword())) {
			userInfo.setPassword(Funs.passwdEncoder(userInfo.getPassword()));
		}
		count = 0;
		if(Strings.isNotBlank(userInfo.getId())) {
			userInfo.setUpdatetime(new Date());
			count = dao.updateIgnoreNull(userInfo);
		}else {
			userInfo.setId(Funs.UUID());
			userInfo.setState(0);
			userInfo.setCreatetime(new Date());
			userInfo.setUpdatetime(new Date());
			count = dao.insert(userInfo)!=null?1:0;
		}
		if(count > 0) {
			return new Resp().ok("操作成功", userInfo);
		}else {
			return new Resp().error("操作失败");
		}
	}
	
	@ApiOperation(value="修改当前账号密码",notes = "需要提供旧密码验证")
	@GetMapping("/modifySelfPwd")
	public Resp<UserInfo> modifySelfPwd(@ApiParam("旧密码(需sm3加密)") @RequestParam(required = false,defaultValue = "") String oldPwd,
			@ApiParam("新密码(需sm3加密)")@RequestParam(required = false,defaultValue = "") String newPwd,
			@ApiParam("用户id")@RequestParam(required = false,defaultValue = "") String id){
		if(Strings.isBlank(id)) {
			return new Resp().error("参数不能空");
		}
		if(Strings.isBlank(oldPwd)) {
			return new Resp().error("旧密码不能空");
		}
		if(Strings.isBlank(newPwd)) {
			return new Resp().error("新密码不能空");
		}
		UserInfo  UserInfo = dao.fetch(UserInfo.class, id);
		if(!Funs.passwdMatches(oldPwd, UserInfo.getPassword())) {
			return new Resp().error("旧密码错误，请重新输入");
		}
		UserInfo.setPassword(Funs.passwdEncoder(newPwd));
		int n =  dao.update(UserInfo);
		if(n > 0) {
			return new Resp().ok("操作成功", UserInfo);
		}else {
			return new Resp().error("操作失败");
		}
	}
	
	@ApiOperation(value="重置子用户密码",notes = "用户给列表中的管理员重置密码用")
	@GetMapping("/resetOtherPwd")
	public Resp<UserInfo> resetOtherPwd(
			@ApiParam("新密码(需sm3加密)") @RequestParam("newPwd") String newPwd,
			@ApiParam("用户id")@RequestParam("id") String id){
		if(Strings.isBlank(id)) {
			return new Resp().error("参数不能空");
		}
		if(Strings.isBlank(newPwd)) {
			return new Resp().error("新密码不能空");
		}
		UserInfo  UserInfo = dao.fetch(UserInfo.class, id);
		if(UserInfo==null) {
			return new Resp().error("用户不存在");
		}
		UserInfo.setPassword(Funs.passwdEncoder(newPwd));
		int n =  dao.update(UserInfo);
		if(n > 0) {
			return new Resp().ok("重置成功", UserInfo);
		}else {
			return new Resp().error("操作失败");
		}
	}
	
	@ApiOperation("管理员列表")
	@GetMapping("/list")
	public Resp<QueryResult<UserInfo>> list(@ApiParam("姓名查找")@RequestParam(name="username", defaultValue = "") String username
			, @ApiParam("账号")@RequestParam(name="account", defaultValue = "") String account
			, @ApiParam("状态默认为-1, -1全部 1禁用 0启用")@RequestParam(name="state", defaultValue = "-1") int state
			, @ApiParam("页数")@RequestParam(name="page", defaultValue = "1") int page
			, @ApiParam("每页显示条数")@RequestParam(name="limit", defaultValue = "10") int limit){
		Cnd cnd = Cnd.where("isdelete", "=", 0);
		Pager pager = dao.createPager(page, limit);
		if(Strings.isNotBlank(username)) {
			cnd.and("username", "like", "%" + username + "%");
		}
		if(Strings.isNotBlank(account)) {
			cnd.and("account", "like", "%" + account + "%");
		}
		if(state != -1) {
			cnd.and("state", "=", state);
		}
		pager.setRecordCount(dao.count(UserInfo.class, cnd));
		List<UserInfo> list = dao.query(UserInfo.class
				, cnd.limit(page, limit).desc("createtime"));
		return new Resp().ok("获取成功", new QueryResult(list, pager));
	}
	
	@ApiOperation(value="删除")
	@DeleteMapping("/delete")
	public Resp<UserInfo> delete(@RequestParam("id") String id){
		if(Strings.isBlank(id)) {
			return new Resp().error("id不能为空");
		}
		int num = dao.delete(UserInfo.class, id);
		if(num > 0) {
			return new Resp().ok("删除成功");
		}else {
			return new Resp().error("删除失败");
		}
	}
	
}
