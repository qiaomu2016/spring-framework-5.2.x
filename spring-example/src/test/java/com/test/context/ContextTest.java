package com.test.context;

import com.test.batis.bean.F;
import com.test.context.bean.A;
import com.test.context.bfpp.B;
import com.test.context.bfpp.C;
import com.test.context.bfpp.X;
import com.test.context.config.ContextConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class ContextTest {

	@Test
	public void testRegisterContext() {
		// AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ContextConfig.class);

		// 注：如果需要根据环境来加载不同的类，那么需要使用下面这种方式：先设置所属环境，再加载类
		// 因为上面 new AnnotationConfigApplicationContext(RootConfig.class) 这种方式，是已经扫描加载了所有的类了，环境变量将不再起作用
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		// 设置环境
		// applicationContext.getEnvironment().setActiveProfiles("dev");

		// 可以注入一个配置类，配置类上会加入组件扫描：@ComponentScan
		applicationContext.register(ContextConfig.class);
//		applicationContext.registerBean(ContextConfig.class,ContextConfig::new);
		// 也可以注入单个类
		// applicationContext.register(UserDao.class);
		applicationContext.scan("com.test.context.listener");
//		applicationContext.register(ABeanListener.class);
//		applicationContext.register(MyEventListener.class);
		applicationContext.refresh();

		A a = applicationContext.getBean(A.class);
		a.getC();
	}

	@Test
	public void defaultContext() {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory(); // bean工厂
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(com.test.context.bean.A.class);
		builder.getBeanDefinition().setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
		beanFactory.registerBeanDefinition("a", builder.getBeanDefinition());

		builder = BeanDefinitionBuilder.genericBeanDefinition(com.test.context.bean.C.class);
		beanFactory.registerBeanDefinition("c", builder.getBeanDefinition());

		AutowiredAnnotationBeanPostProcessor auto = new AutowiredAnnotationBeanPostProcessor();
		auto.setBeanFactory(beanFactory);
//		beanFactory.addBeanPostProcessor(auto);
		beanFactory.getBean(com.test.context.bean.C.class);
		com.test.context.bean.A a = beanFactory.getBean(com.test.context.bean.A.class);
		a.getC();

	}

	@Test
	public void defaultAnnotationConfigApplicationContext() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.addBeanFactoryPostProcessor(new B());
		context.addBeanFactoryPostProcessor(new C());
		context.register(ContextConfig.class);
		context.refresh();
		System.out.println(context.getBean(X.class));
	}

	@Test
	public void xmlBeanFactoryScanContext() {
		ClassPathResource classPathResource = new ClassPathResource("spring-context.xml");
		XmlBeanFactory beanFactory = new XmlBeanFactory(classPathResource);
		A a = beanFactory.getBean(A.class);
		a.getC();
	}


	@Test
	public void ignoreContext() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.scan("com.test.batis.bean");
		context.refresh();
		System.out.println(context.getBean(F.class).getK());
	}
}
