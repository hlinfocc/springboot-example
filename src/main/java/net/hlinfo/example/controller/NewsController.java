package net.hlinfo.example.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.core.query.LambdaQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import net.hlinfo.example.entity.News;
import net.hlinfo.example.entity.UserInfo;
import net.hlinfo.example.utils.Funs;
import net.hlinfo.example.utils.QueryResult;
import net.hlinfo.example.utils.Resp;
import net.hlinfo.mybatis.service.MybatisService;
import net.hlinfo.opt.Func;

@Api(tags = "新闻模块")
@RestController
@RequestMapping("/news")
public class NewsController extends BaseController {
	
	@Autowired
	private Dao dao;

	@Autowired
	private MybatisService MS;
	
	@Autowired
	private SQLManager sqlManager;
	
	@SuppressWarnings("unchecked")
//	@PreAuthorize("hasAnyAuthority('USER')")
	@ApiOperation(value="添加/编辑管理员")
	@PostMapping("/addOrUpdate")
	public Resp<UserInfo> addOrUpdate(@Valid @RequestBody News obj){
		if(obj == null) {
			return new Resp().error("参数不能空");
		}
		int count = 0;
		if(Func.isBlank(obj.getId())) {
			obj.init();
			System.out.println(obj);
			News rs = dao.insert(obj);
			count = rs==null?0:1;
		}else {
			obj.updated();
			count = dao.updateIgnoreNull(obj);
		}
		if(count > 0) {
			return new Resp().ok("操作成功", obj);
		}else {
			return new Resp().error("操作失败");
		}
	}
	
	
	@ApiOperation("管理员列表")
	@GetMapping("/list")
	public Resp<PageResult<News>> list(@ApiParam("关键词")@RequestParam(name="keywords", required = false,defaultValue = "") String keywords
			, @ApiParam("状态默认为-1, -1全部 0草稿，1发布，2撤稿")@RequestParam(name="status", defaultValue = "-1") int status
			, @ApiParam("页数")@RequestParam(name="page", defaultValue = "1") int page
			, @ApiParam("每页显示条数")@RequestParam(name="limit", defaultValue = "10") int limit){
		
		PageRequest<News> request = DefaultPageRequest.of(page, limit);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("keywords",Funs.isBlank(keywords)?"":keywords);
		map.put("status",status);
		System.out.println(map);
		PageResult<News> rslist = sqlManager.pageQuery(SqlId.of("news","list"), News.class,map, request);
		
		return new Resp().ok("获取成功", rslist);
	}
	
	@ApiOperation(value="查询一条")
	@GetMapping("/queryOne")
	public Resp<String> queryOne(@RequestParam("id") String id){
		if(Strings.isBlank(id)) {
			return new Resp().error("id不能为空");
		}
		News obj = dao.fetch(News.class, id);
		if(obj==null) {
			return new Resp().error("内容不存在");
		}
		return new Resp().ok("成功",obj);
	}
	
	@ApiOperation(value="删除")
	@PreAuthorize("hasAnyAuthority('admin')")
	@DeleteMapping("/delete")
	public Resp<String> delete(@RequestParam("id") String id){
		if(Strings.isBlank(id)) {
			return new Resp().error("id不能为空");
		}
		News obj = dao.fetch(News.class, id);
		if(obj==null) {
			return new Resp().error("删除内容不存在");
		}
		obj.deleted();
		int rs = dao.update(obj);
		if(rs > 0) {
			return new Resp().ok("删除成功");
		}else {
			return new Resp().error("删除失败");
		}
	}
	
}
