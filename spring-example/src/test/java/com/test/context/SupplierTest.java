package com.test.context;

import org.junit.Test;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.annotation.PostConstruct;

public class SupplierTest {

	@Test
	public void testSupplier() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(User.class);
		beanDefinition.setInstanceSupplier(SupplierTest::createUser);
		context.registerBeanDefinition(User.class.getSimpleName(), beanDefinition);
		context.refresh();
		System.out.println(context.getBean(User.class).getName());
	}

	private static User createUser() {
		return new User("qm");
	}

	static class User {
		private String name;

		public User(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@PostConstruct
		public void init() {
			System.out.println("user 初始化.....");
		}
	}
}
