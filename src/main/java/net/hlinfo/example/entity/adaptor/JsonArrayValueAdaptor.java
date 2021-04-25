package net.hlinfo.example.entity.adaptor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.json.Json;
import org.postgresql.util.PGobject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

public class JsonArrayValueAdaptor implements ValueAdaptor{
	@Override
	public JSONArray get(ResultSet rs, String colName) throws SQLException {
		// TODO Auto-generated method stub
		PGobject obj = (PGobject) rs.getObject(colName);
		return JSON.parseArray(obj.getValue());
	}
	
	public void set(java.sql.PreparedStatement stat, Object obj, int index) throws SQLException {
		PGobject pgobject = new PGobject();
		pgobject.setType("json");
		pgobject.setValue(Json.toJson(obj));
		stat.setObject(index, pgobject);
	};
}
