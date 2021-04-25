package net.hlinfo.example.mybatis.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import com.alibaba.fastjson.JSON;
/**
 * <ul>
 * <li>
 * <pre>
 * column为字段名，如果sql字段有别名则为别名
 * property为Java实体类属性名
 * <resultMap id="baseMap" type="User">
 *  <result property="description" javaType="com.alibaba.fastjson.JSONArray" column="description" typeHandler="com.jiudingcheng.tzxtapi.mybatis.handler.JsonTypeHandler"/>
 * </resultMap>
 * </pre>
 * </li>
 * <li>
 * <insert id="add" keyProperty="id" useGeneratedKeys="true">
 *  insert into`user` (description)
 *  values (#{description ,typeHandler=com.jiudingcheng.tzxtapi.mybatis.handler.JsonTypeHandler})
 * </insert>
 * </li>
 * <li>
 * <update id="update">
 *   update `user`
 *  set json=  #{description,typeHandler=com.jiudingcheng.tzxtapi.mybatis.handler.JsonTypeHandler}
 *  where id = #{id}
 * </update>
 * </li>
 * </ul>
 * @author hadoop
 *
 * @param <T>
 */
public class JsonTypeHandler<T extends Object> extends BaseTypeHandler<T> {
	private Class<T> clazz;
	public JsonTypeHandler(Class<T> clazz) {
        this.clazz = clazz;
    }
	private static final PGobject jsonObject = new PGobject();
	
    public JsonTypeHandler() {
     }
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        jsonObject.setType("json");
        jsonObject.setValue(JSON.toJSONString(parameter));
        ps.setObject(i, jsonObject);
    }
    @Override
	public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return JSON.parseObject(rs.getString(columnName), clazz);
	}

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return JSON.parseObject(rs.getString(columnIndex), clazz);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return JSON.parseObject(cs.getString(columnIndex), clazz);
    }
	
}
