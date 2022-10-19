package com.test.aop;

import com.test.aop.config.App;
import com.test.aop.service.StudentService;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestAop {
	@Test
	public void xmlTest(){
		ClassPathXmlApplicationContext context =
				new ClassPathXmlApplicationContext("classpath:application-aop.xml");
		StudentService bean = context.getBean(StudentService.class);
		bean.add("com/example");
	}

	@Test
	public void annoTest(){
		AnnotationConfigApplicationContext context =
				new AnnotationConfigApplicationContext(App.class);
		StudentService bean = context.getBean(StudentService.class);
		bean.add("spring anno");
	}
}
