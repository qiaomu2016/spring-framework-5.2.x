package org.spring.aop.test;

import org.junit.Test;
import org.spring.aop.advice.BeforeAdvice;
import org.spring.aop.anno.MyParamter;
import org.spring.aop.anno.MyParamterAnno;
import org.spring.aop.config.App;
import org.spring.aop.service.*;
import org.spring.aop.service.impl.*;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AopTest {

	@Test
	public void testAop(){
		AnnotationConfigApplicationContext context
				= new AnnotationConfigApplicationContext();
		context.register(App.class);
		context.refresh();

		Service service = (Service) context.getBean("service");
		service.method();
		service.methodIntegerArgs(2);
		service.methodIntegerMultiArgs(3,"3");

		Service service1 = (Service) context.getBean("service1");
		service1.method();
		service1.methodIntegerArgs(12);
		service1.methodIntegerMultiArgs(13,"13");


//		Service service = context.getBean(Service.class);
//		service.method();
//		service.methodIntegerArgs(2);
//		service.methodIntegerMultiArgs(3,"3");



		ServiceOther other = context.getBean(ServiceOther.class);
		other.methodOther();


		CglibTestService cglibTestService = context.getBean(CglibTestService.class);
		cglibTestService.method();
	}

	@Test
	public void testIntroductionsAop(){
		AnnotationConfigApplicationContext context
				= new AnnotationConfigApplicationContext();
		context.register(App.class);
		context.refresh();

		UserService userService = context.getBean(UserService.class);
		userService.query();

		DeleteService deleteService = (DeleteService) context.getBean(UserService.class);
		deleteService.delete();
	}


	@Test
	public void testAnnoArgsAop(){
		AnnotationConfigApplicationContext context
				= new AnnotationConfigApplicationContext();
		context.register(App.class);
		context.refresh();

		AnnotationArgsService service = context.getBean(AnnotationArgsService.class);
		service.method1(new MyParamter());
		service.method2(new MyParamterAnno());
		service.method3(new MyParamterAnno(),8);
		service.method4(new MyParamterAnno());
	}


	@Test
	public void testAnnoWithinAop(){
		AnnotationConfigApplicationContext context
				= new AnnotationConfigApplicationContext();
		context.register(App.class);
		context.refresh();

		AnnoWithinService withinService = context.getBean(AnnoWithinService.class);
		withinService.m();

		AnnoWithinServiceNormal withinServiceNormal = context.getBean(AnnoWithinServiceNormal.class);
		withinServiceNormal.m();

	}


	@Test
	public void testAnnoAnnotationAop(){
		AnnotationConfigApplicationContext context
				= new AnnotationConfigApplicationContext();
		context.register(App.class);
		context.refresh();

		AnnoAnnotationService annoAnnotationService = context.getBean(AnnoAnnotationService.class);
		annoAnnotationService.m();

		AnnoAnnotationServiceNormal normal = context.getBean(AnnoAnnotationServiceNormal.class);
		normal.m();

	}



	@Test
	public void testAopCode(){
		AnnotationConfigApplicationContext context
				= new AnnotationConfigApplicationContext();
		context.register(App.class);
		context.refresh();

		AopService a = (AopService) context.getBean("a");
		a.m();

//		AopService b = (AopService) context.getBean("b");
//		b.m();

	}

	@Test
	public void testCustomAop(){
		ProxyFactory pf = new ProxyFactory();
		pf.setInterfaces(AopService.class);
		pf.addAdvice(new BeforeAdvice());
		pf.setTarget(new BAopServiceImpl());
		//pf.setProxyTargetClass(true);
		// createAopProxy()  jdk  cglib
		AopService service = (AopService) pf.getProxy();
		//通知是怎么执行的
		//目标对象的m方法怎么调用的？
		service.m1(9);
	}


}
