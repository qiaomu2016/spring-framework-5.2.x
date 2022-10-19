package com.example;

import com.example.dao.impl.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
@Slf4j(topic = "e")
public class AppConfigTest {

	@Test
	public void testAppConfig() {

		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
//		// 注：如果需要根据环境来加载不同的类，那么需要使用下面这种方式：先设置所属环境，再加载类
//		// 因为上面 new AnnotationConfigApplicationContext(RootConfig.class) 这种方式，是已经扫描加载了所有的类了，环境变量将不再起作用
//		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
//		// 设置环境
//		applicationContext.getEnvironment().setActiveProfiles("dev");
//
//		// 可以注入一个配置类，配置类上会加入注释扫描：@ComponentScan
//		applicationContext.register(AppConfig.class);
//		// 也可以注入单个类
//		applicationContext.register(UserDao.class);
//		applicationContext.scan("com.example");
//		applicationContext.refresh();
		applicationContext.getBean("userDao",UserDao.class);

	}
}
