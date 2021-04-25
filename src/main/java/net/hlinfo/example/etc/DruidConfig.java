package net.hlinfo.example.etc;

import java.util.Date;

import javax.sql.DataSource;

import org.beetl.core.GroupTemplate;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.ConnectionSourceHelper;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.util.Daos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;

import net.hlinfo.example.entity.UserInfo;
import net.hlinfo.example.utils.Funs;


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
	 * @return
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
	
	public void initDB(Dao dao){
		int user = dao.count(UserInfo.class);
		if(user==0) {
			UserInfo userinfo = new UserInfo();
			userinfo.setId(Funs.UUID());
			userinfo.setUsername("超级管理员");
			userinfo.setAccount("admin");
			userinfo.setPassword(Funs.passwdEncoder(Funs.sm3("123456")));
			userinfo.setEmail("admin@localhost");
			userinfo.setLoginip("");
			userinfo.setLogintime("");
			userinfo.setLastLoginIp("");
			userinfo.setLastLoginTime("");
			userinfo.setRemark("超级管理员，拥有系统中最高权限");
			userinfo.setState(0);
			userinfo.setCreatetime(new Date());
			userinfo.setUpdatetime(new Date());
			userinfo.setIsdelete(0);
			userinfo = dao.insert(userinfo);
			
		}
	}
	

}
