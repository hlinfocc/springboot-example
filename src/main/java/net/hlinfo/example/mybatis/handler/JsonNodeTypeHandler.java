package net.hlinfo.example.mybatis.handler;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@MappedTypes(JsonNode.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonNodeTypeHandler extends BaseTypeHandler<JsonNode> {
	private static Logger log = LoggerFactory.getLogger(JsonNodeTypeHandler.class);
	
	private final ObjectMapper objectMapper;

	public JsonNodeTypeHandler(ObjectMapper objectMapper) {
		super();
		this.objectMapper = objectMapper;
	}

	@Override
    public void setNonNullParameter(PreparedStatement ps, int i, JsonNode parameter, JdbcType jdbcType) throws SQLException {
        String json = parameter.toString();
        ps.setString(i, json);
    }
 
    private JsonNode read(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonParseException e) {
            log.error("JSON parse failed", e);
            return null;
        } catch (IOException e) {
            // should not occur, no real i/o...
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
    @Override
    public JsonNode getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return read(json);
    }
 
    @Override
    public JsonNode getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return read(json);
    }
 
    @Override
    public JsonNode getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return read(json);
    }
}
