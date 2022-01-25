package net.hlinfo.example.etc;

import java.util.Properties;

import javax.sql.DataSource;

import org.beetl.core.GroupTemplate;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.PostgresStyle;
import org.beetl.sql.core.engine.template.Beetl;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;
import org.beetl.sql.core.engine.template.SQLTemplateEngine;
import org.beetl.sql.core.loader.MarkdownClasspathLoader;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.starter.SQLManagerCustomize;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.util.Daos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;

import net.hlinfo.example.entity.UserInfo;
import net.hlinfo.example.utils.BeetlBlankFun;
import net.hlinfo.example.utils.Funs;
import net.hlinfo.example.utils.Vo2PgsqlFieldFun;


@Configuration
public class DruidConfig {
	
	@Value("${spring.profiles.active}")
	private String profiles;
	
	 @Bean(destroyMethod = "close", initMethod = "init")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }

	/**
	 * 配置Nutz Dao操作数据库 
	 * @param druidDataSource
	 * @return Dao对象
	 */
	@Bean
	public Dao dao(DataSource druidDataSource) {
		Dao dao = new NutDao(druidDataSource);
		Daos.createTablesInPackage(dao, "net.hlinfo.example.entity", false);
		if(Funs.equals(profiles, "dev") || Funs.equals(profiles, "test")) {
			this.initDB(dao);
		}
		return dao;
	}
	/**
	 * 配置BeetlSQL3 操作数据库
	 * @return SQLManagerCustomize
	 */
	@Bean
    public SQLManagerCustomize mySQLManagerCustomize(){
        return new SQLManagerCustomize(){
            @Override
            public void customize(String sqlMangerName, SQLManager sqlManager) {
            	//初始化sql，这里也可以对sqlManager进行修改
				//DBInitHelper.executeSqlScript(sqlManager,"db/schema.sql");
            	//数据库风格，因为用的是PostgresSQL用PostgresStyle,MySQL用MySqlStyle
        		sqlManager.setDbStyle(new PostgresStyle());
        		//命名转化，数据库表和列名下划线风格，转化成Java对应的首字母大写，比如create_time 对应ceateTime
        		sqlManager.setNc(new UnderlinedNameConversion());
        		//拦截器，非必须，这里设置一个debug拦截器，可以详细查看执行后的sql和sql参数
        		sqlManager.setInters(new Interceptor[]{new DebugInterceptor()});
        		//注入自定义函数
        		BeetlTemplateEngine beetlTemplateEngine = (BeetlTemplateEngine)sqlManager.getSqlTemplateEngine();
        		GroupTemplate gt = beetlTemplateEngine.getBeetl().getGroupTemplate();
        		gt.registerFunction("vo2PgsqlField", new Vo2PgsqlFieldFun());
        		gt.registerFunction("hasBlank", new BeetlBlankFun());
        		gt.registerFunction("isNotBlank", new org.beetl.ext.fn.IsNotEmptyExpressionFunction());
            }
        };
    }
	
	public void initDB(Dao dao){
		int user = dao.count(UserInfo.class);
		if(user==0) {
			UserInfo userinfo = new UserInfo();
			userinfo.init();
			userinfo.setUsername("超级管理员");
			userinfo.setAccount("admin");
			userinfo.setPassword(Funs.passwdEncoder(Funs.sm3("123456")));
			userinfo.setEmail("admin@localhost");
			userinfo.setThisLoginIp("");
			userinfo.setThisLoginTime("");
			userinfo.setLastLoginIp("");
			userinfo.setLastLoginTime("");
			userinfo.setRemark("超级管理员，拥有系统中最高权限");
			userinfo.setStstus(0);
			userinfo = dao.insert(userinfo);
			
		}
	}
	

}
