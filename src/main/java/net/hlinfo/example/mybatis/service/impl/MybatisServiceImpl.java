package net.hlinfo.example.mybatis.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.springframework.stereotype.Service;

import net.hlinfo.example.mybatis.dao.MybatisDao;
import net.hlinfo.example.mybatis.service.MybatisService;

@Service
public class MybatisServiceImpl implements MybatisService {
	@Resource
    private MybatisDao baseDao;
	@Override
	public <T> List<T> queryList(String mybitsSqlId, Class<T> classOfT, Object object) {
		return this.baseDao.queryList(mybitsSqlId, classOfT, object);
	}

	@Override
	public <T> List<T> queryList(String mybitsSqlId, Class<T> classOfT, Map<String, Object> map) {
		return this.baseDao.queryList(mybitsSqlId, classOfT, map);
	}

	@Override
	public <T> List<T> queryList(String mybitsSqlId,Class<T> classOfT) {
		return this.baseDao.queryList(mybitsSqlId,classOfT);
	}

	@Override
	public int count(String mybitsSqlId) {
		// TODO Auto-generated method stub
		return this.baseDao.count(mybitsSqlId);
	}

	@Override
	public int count(String mybitsSqlId, Object object) {
		// TODO Auto-generated method stub
		return this.baseDao.count(mybitsSqlId, object);
	}

	@Override
	public <T> T find(String mybitsSqlId, Class<T> classOfT) {
		// TODO Auto-generated method stub
		return this.baseDao.find(mybitsSqlId, classOfT);
	}

	@Override
	public <T> T find(String mybitsSqlId, Class<T> classOfT, Map<String, Object> map) {
		// TODO Auto-generated method stub
		return this.baseDao.find(mybitsSqlId, classOfT, map);
	}

	@Override
	public <T> T find(String mybitsSqlId, Class<T> classOfT, Object object) {
		// TODO Auto-generated method stub
		return this.baseDao.find(mybitsSqlId, classOfT, object);
	}

	@Override
	public boolean save(String mybitsSqlId, Object object) {
		// TODO Auto-generated method stub
		return this.baseDao.save(mybitsSqlId, object);
	}

	@Override
	public boolean update(String mybitsSqlId, Object object) {
		// TODO Auto-generated method stub
		return this.baseDao.update(mybitsSqlId, object);
	}
	
	@Override
	public <T> QueryResult pageList(String mybitsSqlId,String mybitsCountSqlId, Class<T> classOfT, Map<String,Object> map, int page, int limit) {
		// TODO Auto-generated method stub
		page = (page==0)?1:page;
		limit = (limit==0)?20:limit;
		int start = (page-1)*limit;
		map.put("start", start);
		map.put("limit", limit);
		List<T> list = this.baseDao.queryList(mybitsSqlId, classOfT, map);
		int count = this.baseDao.count(mybitsCountSqlId, map);
		Pager pager = new Pager(page, limit);
		pager.setRecordCount(count);
		return new QueryResult(list, pager);
	}

	@Override
	public <T> QueryResult pageList(String mybitsSqlId, String mybitsCountSqlId, Class<T> classOfT, String fields,
			Map<String, Object> map, int page, int limit) {
		page = (page==0)?1:page;
		limit = (limit==0)?20:limit;
		int start = (page-1)*limit;
		map.put("start", start);
		map.put("limit", limit);
		map.put("TableField", fields);
		List<T> list = this.baseDao.queryList(mybitsSqlId, classOfT, map);
		int count = this.baseDao.count(mybitsCountSqlId, map);
		Pager pager = new Pager(page, limit);
		pager.setRecordCount(count);
		return new QueryResult(list, pager);
	}

	@Override
	public <T> T find(String mybitsSqlId, Class<T> classOfT, String fields, Map<String, Object> map) {
		// TODO Auto-generated method stub
		map.put("TableField", fields);
		return this.baseDao.find(mybitsSqlId, classOfT, map);
	}

	@Override
	public <T> List<T> queryList(String mybitsSqlId, Class<T> classOfT, String fields, Map<String, Object> map) {
		// TODO Auto-generated method stub
		map.put("TableField", fields);
		return this.baseDao.queryList(mybitsSqlId, classOfT, map);
	}

	@Override
	public boolean delete(String mybitsSqlId) {
		// TODO Auto-generated method stub
		return this.baseDao.delete(mybitsSqlId);
	}

	@Override
	public boolean delete(String mybitsSqlId, Object object) {
		// TODO Auto-generated method stub
		return this.baseDao.delete(mybitsSqlId, object);
	}

}
