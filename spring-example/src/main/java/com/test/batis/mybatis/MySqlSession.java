package com.test.batis.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MySqlSession {
	/**
	 * 模拟一个mybatis
	 * 当然我们不可能去把mybatis的所有功能模拟出来；为了更好的研究spring源码，我们只需要模拟他的核心功能——通过JDK动态代理技术产生代理对象；然后执行方法与之对应的sql语句
	 * <p>
	 * 1、实现clazz接口
	 * 2、实现clazz接口当中的所有方法
	 * 3、这些方法的逻辑需要完成对改方法上面的sql语句的执行
	 * (ClassLoader loader,Class<?>[] interfaces,InvocationHandler h
	 */
	public static Object getMapper(Class<?> clazz) {
		ClassLoader classLoader = MySqlSession.class.getClassLoader();
		Class<?>[] classes = new Class[]{clazz};
		return Proxy.newProxyInstance(classLoader, classes, new MyTestInvocationHandler());
	}

	@Slf4j(topic = "e")
	static class MyTestInvocationHandler implements InvocationHandler {

		// 获取当前执行的方法对应的sql语句
		// 执行这些sql语句
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			// 处理toString、hashcode等Object方法
			if (method.getDeclaringClass().equals(Object.class)) {
				method.invoke(this, args);
			}
			Select select = method.getAnnotation(Select.class);
			String sql = select.value()[0];
			log.debug("假装已经连接数据库了 conn db");
			log.debug("假装执行查询 execute sql:{}", sql);
			log.debug("假装根据类型返回了真实对象----");
			return null;
		}
	}
}
