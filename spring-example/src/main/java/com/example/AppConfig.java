package com.example;

import com.example.dao.impl.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.example.dao")
@Slf4j(topic = "e")
public class AppConfig {

	@Bean
	public UserDao userDao1() {
		log.info("---userDao1 init---");
		return new UserDao();
	}

	@Bean
	public UserDao userDao() {
		// ①：如果AppConfig类没有加@Configuration注解，IndexDao1会实例化两次
		// ②：如果AppConfig类加了@Configuration注解，那么IndexDao1是单例的，只实例化一次
		// （因为通过cglib对全注解的配置类进行了增强，在内部调用indexDao1()的时候，会先直接从beanFactory中拿）
		userDao1();
		return new UserDao();
	}
}
