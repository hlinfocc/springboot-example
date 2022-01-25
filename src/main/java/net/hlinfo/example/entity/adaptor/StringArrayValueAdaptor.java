package net.hlinfo.example.entity.adaptor;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.jdbc.ValueAdaptor;
import org.postgresql.jdbc.PgArray;
import org.postgresql.util.PGobject;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;

public class StringArrayValueAdaptor implements ValueAdaptor{
	@Override
	public String[] get(ResultSet rs, String colName) throws SQLException {
		PgArray obj = (PgArray) rs.getObject(colName);
		JSONArray ja =  JSONUtil.parseArray(obj.getArray());
		String[] arr = new String[ja.size()];
		for(int i=0;i<ja.size();i++) {
			arr[i] = ja.get(i).toString();
		}
		return arr;
	}
	
	public void set(java.sql.PreparedStatement stat, Object obj, int index) throws SQLException {
		//PGobject pgobject = new PGobject();
		//pgobject.setType(null);
		stat.setObject(index, obj);
	};
}
