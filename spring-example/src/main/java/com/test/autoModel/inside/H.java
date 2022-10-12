package com.test.autoModel.inside;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 内部类的实例化现象
 */
public class H {

	 class J {
		public J(H h){

		}
	}

	// J作为H的内部类，如果直接单纯的把J注册给spring容器去实例化J会失败；可以理解为J是H的一个成员变量；
	// H没有注册给spring容器，站在spring角度而言就是没有H的对象，那么成员变量自然无法实例化；
	// 原理是因为在实例化J的时候，spring根据J的构造方法会推断出来一个需要带H的的构造方法public J(H h){};
	// 而且spring会试用这个构造方法去实例化J，实例化J的过程当中会去 getBean(H.class)；故而失败；
	// 解决办法：
	// 1、把J定义成为一个静态的内部类；这样就不需要H的对象就能实例化J
	// 2、给J提供一个带H的构造方法，并且把H注册给spring容器
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(H.class);
		context.register(J.class);
		context.refresh();

		context.getBean(J.class);
	}
}
