package com.example;

import com.example.app.App;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

@Slf4j(topic = "e")
public class TestSpringBasic {

	@Test
	public void test() throws IOException {
		AnnotationConfigApplicationContext context
				= new AnnotationConfigApplicationContext();
		context.register(App.class);
		//context.addBeanFactoryPostProcessor(new Z());



//		ConfigurableEnvironment environment = context.getEnvironment();
//		ResourcePropertySource rps = new ResourcePropertySource("classpath:application.properties");
//		environment.getPropertySources().addLast(rps);
		context.refresh();


		//System.out.println(environment.getProperty("k1"));

	}






	@Test
	public void testProperties() throws IOException {
		AnnotationConfigApplicationContext context
				= new AnnotationConfigApplicationContext();
		context.getEnvironment().getPropertySources().addLast(new ResourcePropertySource("classpath:application.properties"));
		String k1 = context.getEnvironment().getProperty("k1");
		log.debug("k1:[{}]",k1);
	}
}
