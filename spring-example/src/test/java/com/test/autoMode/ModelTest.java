package com.test.autoMode;

import com.test.autoModel.config.ModelConfig;
import com.test.autoModel.defaults.A;
import com.test.autoModel.defaults.ExampleBean;
import com.test.autoModel.defaults.M;
import com.test.autoModel.defaults.N;
import com.test.autoModel.inject.F;
import com.test.autoModel.inject.I;
import com.test.autoModel.lookup.LA;
import com.test.autoModel.lookup.LC;
import com.test.autoModel.order.*;
import com.test.autoModel.statics.Config;
import com.test.autoModel.statics.ObjectFactory;
import com.test.autoModel.statics.ObjectStaticFactory;
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
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(ModelConfig.class);
		// 改变bean为m的注入模型为按照类型自动注入属性，因此注入的属性不需要加 @Autowired 或者 @Resource 注解，框架会自动注入属性
		context.register(ModelBeanFactoryPostProcessor.class);
		context.refresh();
		N n = context.getBean(M.class).getN();
		ExampleBean exampleBean = context.getBean(A.class).getExampleBean();
		log.debug("n ==[{}]", n);
	}


	/**
	 * 测试@Autowired的注入基本原理
	 * 先type继而name,如果有多个再name
	 *
	 * @Autowired不会改变bean的注入模型（默认情况下bean的注入模型还是AUTOWIRE_NO）；@Autowired算是一种半自动注入；因为他只需要程序员告诉spring需要注入的属性或者方法， 而不需要程序员告诉spring需要注入的属性或者方法他的值到底是哪个bean；@Autowired会根据自己的规则去查找这个bean，所以只能算作半自动注入
	 * @Autowired和@Resource能完成一样的功能；只不过前者是首先根据类型查找bean；如果没有找到报错（默认情况下@Autowired是一定需要注入一个bean的）； 如果查找到一个则用找到的这一个完成注入；如果查找到多个；先把这个多个放到map当中；继而根据属性的名字去map当中去确定唯一的一个bean；能确定则使用确定的这个；如果map当中通过名字还是无法确定则报错；
	 * @Resource在没有配置name的情况下首先根据名字查找；如果名字能查找到则返回这个查找到的（spring容器的原则是name唯一；所以不存在通过名字能查找到多个的情况）； 如果通过名字查找不到（需要注意的是这里的前提是没有配置name的情况，spring觉得名字无所谓）；因为对名字无要求，所以会再根据类型查找；
	 * 那么走的就是@Autowired这一套；如果配置了名字，spring觉得对名字有严格要求，所以只能根据你配置的名字查找；如果查找不到则报错，找到了则用；不会走@Autowired这一套了
	 */
	@Test
	public void autowiredModel() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.scan("com.test.autoModel.inject");
		context.refresh();
		I i = context.getBean(F.class).getI();
		log.debug("i==[{}]", i);
	}


	/**
	 * 测试@Order注解的功能
	 * 不会影响bean的扫描顺序，具体需要看spring源码内部有没有对他做解析
	 *
	 * @Order注解说明：
	 * @Order注解spring如没有特殊处理是不会影响类的实例化和扫描顺序；比如spring Aop当中通知的执行对于这个注解做了特殊处理所以这个注解能影响通知的执行顺序；
	 * 但是大多数情况下这个注解是没有被特殊处理的；比如你给一个bean上面加这个注解，基本是没什么效果的；
	 * 需要说的是如果你给一个数组或者List集合当中注入一些bean；那么这些bean在被注入的时候会根据改注解的值进行排序进入集合当中；值越小则顺序越高
	 * @Order的优先级低于Ordered低于PriorityOrdered； 换言之上面说集合当中的类的顺序如果实现了PriorityOrdered接口则先按PriorityOrdered的规则排序，如果没有实现PriorityOrdered，
	 * 但是实现了Ordered则按Ordered排序，如果么有实现Ordered最后才是@Order
	 * <p>
	 * spring加载类=spring扫描+spring实例化bean
	 * 无论是spring的扫描还是spring实例化类的顺序都是默认按照类名的字母顺序来的；和bean的名字无关；
	 * 比如你的类名是A.java，但是你给这个A取了一个bean的名字z，那么spring加载的时候是按照A来的不是z
	 */
	@Test
	public void orderModel() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
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
	 * <p>
	 * spring提供了静态工厂方法和实例工厂方法以及supplier这三种机制来实例化一个bean；
	 * 1.静态工厂方法和实例工厂的区别在于静态工厂方法不需要工厂类实例化；也就是工厂类不需要存在spring容器当中；
	 * 2.实例工厂则相反，需要工厂类被实例化，且必须是一个bean（存在spring容器当中）
	 * 3.supplier是上面两张方法的改进或者说替代，supplier既能实现静态也能实现实例；效率高于前面两张；因为前面两种都是基于java的反射机制；
	 * 而supplier是java8的表达式，底层就是方法的调用不是反射
	 */
	@Test
	public void staticsModel() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.scan("com.test.autoModel.statics");
//		context.register(Config.class);

		// ①：测试静态工厂方法
//		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
//		beanDefinition.setBeanClass(ObjectStaticFactory.class);
//		beanDefinition.setFactoryMethodName("instanceObject");
//		context.registerBeanDefinition("a", beanDefinition);

		// ②：测试实例工厂方法
//		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
//		context.register(ObjectFactory.class); // 需要注入工厂类
//		beanDefinition.setFactoryBeanName("objectFactory");
//		beanDefinition.setFactoryMethodName("instanceObject");
//		context.registerBeanDefinition("a", beanDefinition);

		// ③：Supplier
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setInstanceSupplier(SupplierFactory::getObject);  // SupplierFactory不需要存在spring容器当中
		context.registerBeanDefinition("a", beanDefinition);

		context.refresh();

	}


	/**
	 * 测试lookup
	 * <p>
	 * LA是单例，LB是原型；如果在单例LA当中多次使用LB的bean对象；会得到相同的LB对象，则失去了LB作为原型的意义；
	 * 解决办法1、可以让LA实现ApplicationContextAware接口，从而获取ApplicationContext对象；每次在LA当中使用LB对象的时候通过ApplicationContext对象调用getBean(LB)方法获取；
	 * 这每次获取出来的LB对象都是通过spring容器产生的；达到原型的目的；但是这种方法的依赖性太强
	 * <p>
	 * 解决方法2、使用@Lookup注解。@Lookup的原理就是代理，也就是LC这个类被代理了（cglib），其中的createLB()方法被增强了；每次调用createLB()方法都会返回一个新的lb对象
	 */
	@Test
	public void lookupModel() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.scan("com.test.autoModel.lookup");
		context.refresh();
		context.getBean(LA.class).printInfo();
		context.getBean(LC.class).printInfo();
		// 输出结果如下所示：
		// LA.java 行数=15 09:48:22.671 [main] DEBUG e - lb-[com.test.autoModel.lookup.LB@4f49f6af]
		// LA.java 行数=16 09:48:22.684 [main] DEBUG e - lb-[com.test.autoModel.lookup.LB@4f49f6af]
		// LC.java 行数=13 09:48:22.685 [main] DEBUG e - lb-[com.test.autoModel.lookup.LB@41f69e84]
		// LC.java 行数=15 09:48:22.686 [main] DEBUG e - lb-[com.test.autoModel.lookup.LB@7975d1d8]
	}


	/**
	 * 测试 dependsOn的作用
	 */
	@Test
	public void dependsOnModel() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.scan("com.test.autoModel.dependsOn");
		context.refresh();
	}
}
