package com.test.batis.config;

import com.test.batis.util.MyImportBeanDefinitionRegistrar;
import com.test.batis.util.MyScan;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com.test.batis")
//@MapperScan("com.test.batis.dao")
//@ImportResource("classpath:spring-batis.xml")
@Import(MyImportBeanDefinitionRegistrar.class)  // 模拟
//@MyScan
public class BatisConfig {

	@Bean
	public DataSource dataSource(){
		DriverManagerDataSource driverManagerDataSource
				= new DriverManagerDataSource();
		driverManagerDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		driverManagerDataSource.setPassword("123456");
		driverManagerDataSource.setUsername("root");
		driverManagerDataSource.setUrl("jdbc:mysql://192.168.56.101:3306/micro?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
		return driverManagerDataSource;
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(dataSource());
		return factoryBean.getObject();
	}


//	@Bean
//	public TMapper tMapper(){
//		TMapper mapper = (TMapper) MySqlSession.getMapper(TMapper.class);
//		return mapper;
//	}
}
