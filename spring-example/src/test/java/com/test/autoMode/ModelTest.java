package com.test.autoMode;

import com.test.autoModel.config.ModelConfig;
import com.test.autoModel.defaults.A;
import com.test.autoModel.inject.F;
import com.test.autoModel.inject.I;
import com.test.autoModel.lookup.LC;
import com.test.autoModel.order.*;
import com.test.autoModel.statics.ObjectFactory;
import com.test.autoModel.statics.SupplierFactory;
import com.test.autoModel.util.ModelBeanFactoryPostProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

/**
 * 测试注入模型
 */

@Slf4j(topic = "e")
public class ModelTest {

	/**
	 * 测试注入模型对bean的影响
	 * 包括实例化、或者一些高级特性
	 */
	@Test
	public void defaultModel() {
		AnnotationConfigApplicationContext
				context = new AnnotationConfigApplicationContext();
		context.register(ModelConfig.class);
		// 改变了A的注入模型 spring当中的注入模型很重要
		context.register(ModelBeanFactoryPostProcessor.class);
		context.refresh();
//		N n = context.getBean(M.class).getN();
//		ExampleBean exampleBean = context.getBean(A.class).getExampleBean();
//		log.debug("n ==[{}]",n );
	}


	/**
	 * 测试@Autowired的注入基本原理
	 * 先type继而name,如果有多个再name
	 */
	@Test
	public void autowiredModel() {
		AnnotationConfigApplicationContext
				context = new AnnotationConfigApplicationContext();
		context.scan("com.test.autoModel.inject");
		context.refresh();
		//singletonObjects.get("f")
		I i = context.getBean(F.class).getI();
		log.debug("i==[{}]", i);

		//K k = context.getBean(F.class).getK();
	}


	/**
	 * 测试@Order注解的功能
	 * 不会影响bean的扫描顺序，具体需要看spring源码内部有没有对他做解析
	 *
	 * @Order注解说明：
	 * @Order注解spring如没有特殊处理是不会影响类的实例化和扫描顺序；比如spring Aop当中通知的执行对于这个注解做了特殊处理所以这个注解能影响通知的执行顺序；
	 * 但是大多数情况下这个注解是没有被特殊处理的；比如你给一个bean上面加这个注解，基本是没什么效果的；
	 * 需要说的是如果你给一个数组或者List集合当中注入一些bean；那么这些bean在被注入的时候会根据改注解的值进行排序进入集合当中；值越小则顺序越高
	 * @Order的优先级低于Ordered低于PriorityOrdered；
	 * 换言之上面说集合当中的类的顺序如果实现了PriorityOrdered接口则先按PriorityOrdered的规则排序，如果没有实现PriorityOrdered，
	 * 但是实现了Ordered则按Ordered排序，如果么有实现Ordered最后才是@Order
	 *
	 * spring加载类=spring扫描+spring实例化bean
	 * 无论是spring的扫描还是spring实例化类的顺序都是默认按照类名的字母顺序来的；和bean的名字无关；
	 * 比如你的类名是A.java，但是你给这个A取了一个bean的名字z，那么spring加载的时候是按照A来的不是z
	 *
	 */
	@Test
	public void orderModel() {
		AnnotationConfigApplicationContext
				context = new AnnotationConfigApplicationContext();
		context.scan("com.test.autoModel.order");
		context.refresh();

		List<E> beanFactoryPostProcessor = context.getBean(T.class).getBeanFactoryPostProcessor();
		for (E e : beanFactoryPostProcessor) {
			e.orderList();
		}
		//  输出结果：
		// ABeanFactoryPostProcessor.java 行数=19 19:21:10.931 [main] DEBUG e - ==constructor bean a
		// BBeanFactoryPostProcessor.java 行数=17 19:21:10.936 [main] DEBUG e - ==constructor bean b
		// CBeanFactoryPostProcessor.java 行数=19 19:21:10.937 [main] DEBUG e - ==constructor bean c
		// CBeanFactoryPostProcessor.java 行数=38 19:21:10.938 [main] DEBUG e - InitializingBean init method bean c
		// ABeanFactoryPostProcessor.java 行数=38 19:21:10.939 [main] DEBUG e - execute postProcessBeanFactory a order=6  // 按照类名的字母顺序
		// BBeanFactoryPostProcessor.java 行数=32 19:21:10.941 [main] DEBUG e - execute postProcessBeanFactory b order=5
		// CBeanFactoryPostProcessor.java 行数=33 19:21:10.941 [main] DEBUG e - execute postProcessBeanFactory c order=7
		// Z1.java 行数=19 19:21:11.100 [main] DEBUG e - order-5
		// Z1.java 行数=24 19:21:11.102 [main] DEBUG e - annotation init bean z1	 // 按照类名的字母顺序
		// Z2.java 行数=14 19:21:11.103 [main] DEBUG e - order-4
		// Z2.java 行数=19 19:21:11.104 [main] DEBUG e - annotation init bean z2
		// Z3.java 行数=15 19:21:11.105 [main] DEBUG e - order-6
		// Z3.java 行数=20 19:21:11.106 [main] DEBUG e - annotation init bean z3
		// 说明：给一个数组或者List集合当中注入一些bean；那么这些bean在被注入的时候会根据改注解的值进行排序进入集合当中
		// E.java 行数=15 19:21:11.133 [main] DEBUG e - List Order postProcessBeanFactory BBeanFactoryPostProcessor order=5
		// E.java 行数=15 19:21:11.133 [main] DEBUG e - List Order postProcessBeanFactory ABeanFactoryPostProcessor order=6
		// E.java 行数=15 19:21:11.133 [main] DEBUG e - List Order postProcessBeanFactory CBeanFactoryPostProcessor order=7
	}


	/**
	 * 测试工厂方法实例工厂方法、supplier
	 * spring优先选用supplier
	 */
	@Test
	public void staticsModel() {
		AnnotationConfigApplicationContext
				context = new AnnotationConfigApplicationContext();
		//context.scan("com.test.autoModel.statics");
//		context.register(Config.class);
		context.register(ObjectFactory.class);
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();


		beanDefinition.setBeanClass(A.class);


//

		//不需要存在spring容器当中
		//SupplierFactory supplierFactory = new SupplierFactory();
		beanDefinition.setInstanceSupplier(SupplierFactory::getObject);

		beanDefinition.setFactoryBeanName("objectFactory");
		beanDefinition.setFactoryMethodName("instanceObject");

		context.registerBeanDefinition("a", beanDefinition);
		context.refresh();
	}


	/**
	 * 测试lookup
	 */
	@Test
	public void lookupModel() {
		AnnotationConfigApplicationContext
				context = new AnnotationConfigApplicationContext();
		context.scan("com.test.autoModel.lookup");
		context.refresh();
		//context.getBean(LA.class).printInfo();

		context.getBean(LC.class).printInfo();

	}


	/**
	 * 测试 dependsOn的作用
	 */
	@Test
	public void dependsOnModel() {
		AnnotationConfigApplicationContext
				context = new AnnotationConfigApplicationContext();
		context.scan("com.test.autoModel.dependsOn");
		context.refresh();

	}
}
