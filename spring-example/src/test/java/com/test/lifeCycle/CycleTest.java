package com.test.lifeCycle;

import com.test.lifeCycle.bean.N;
import com.test.lifeCycle.config.LifeCycleConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j(topic = "e")
public class CycleTest {

	@Test
	public void defaultCycle() {
		AnnotationConfigApplicationContext context
				= new AnnotationConfigApplicationContext(LifeCycleConfig.class);
		try {
			context.getBean(N.class).afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		context.getBean(F.class).m0();
//		context.getBean(E.class).m0();
	}
}
