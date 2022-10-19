package org.spring.introspect;

import org.junit.Test;
import org.spring.introspect.bean.AnnotationAutowiredFiledBeanTest;
import org.spring.introspect.bean.IntrospectDemoBeanTest;
import org.spring.introspect.bean.SpringBeanInfoTest;
import org.spring.introspect.config.App;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class InstrospectTest {
	//属性名字和对于的描述符
	Map<String,PropertyDescriptor> propertyDescriptors = new HashMap<>();
	@Test
	public void testIntrospect(){
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(IntrospectDemoBeanTest.class);
			PropertyDescriptor[] ps = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor p : ps) {
				propertyDescriptors.put(p.getName(),p);
			}
			PropertyDescriptor age = propertyDescriptors.get("age");

			Method ageSetter = propertyDescriptors.get("age").getWriteMethod();


		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
	}



	@Test
	public void testSpringInject() throws NoSuchFieldException, IntrospectionException {
		AnnotationConfigApplicationContext context
				= new AnnotationConfigApplicationContext();
		context.register(App.class);
		context.refresh();
		context.getBean(SpringBeanInfoTest.class).printf();

		/**
		 * 1、在一个属性上面加一个@Autowired注解
		 */

//		Class clazz = null;
//		Field autowiredFiledBeanTest = clazz.getDeclaredField("autowiredFiledBeanTest");
//		autowiredFiledBeanTest.setAccessible(true);
		//autowiredFiledBeanTest.set(this,getBean(AnnotationAutowiredFiledBeanTest.class));
		/**
		 * 2、在一setter个方法上面加一个@Autowired注解
		 * 方法参数所对于的bean
		 *
		 * 2.1如果注入模型是1、2 自动注入那么spring底层采用的是java的自省机制发现setter方法然后调用执行
		 * 也就是说方法上面的@Autowierd注解无用
		 *
		 * 2.2如果注入模型为0 那么则是和在属性上面加注解差不多，底层查找所有加了@Autowired注解的方法，然后反射调用
		 * method.invoke()
		 */
		//-------------------------------------------------------

		/**
		 * 3、提供一个setter方法，继而把该bean的注入模型改成1、2 自动注入
		 * 3.1  注入模型是自动注入 则是java的内省机制
		 *
		 * 3.2 注入模型为0  则这个方法spring忽略
		 */
//		BeanInfo beanInfo = Introspector.getBeanInfo(SpringBeanInfoTest.class);
//		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
//		//2个
//		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
//			Method writeMethod = propertyDescriptor.getWriteMethod();
//			//writeMethod.invoke(this,getBean(parma))
//		}

	}
}
