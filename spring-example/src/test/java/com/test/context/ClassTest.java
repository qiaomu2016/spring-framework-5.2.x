package com.test.context;

public class ClassTest {

	/**
	 * getEnclosingClass()用于获取对应类的直接外部类Class对象，如果对应类是顶层类，则此方法返回 null。
	 * getDeclaringClass()用于获取对应类的声明类Class对象。如果此 Class 对象所表示的类或接口是另一个类的成员，则返回的 Class 对象表示该对象的声明类。
	 * 						如果该类或接口不是其他类的成员，则此方法返回 null。如果此 Class 对象表示一个数组类、基本类型或 void，则此方法返回 null。
	 * 区别：两者的区别在于匿名内部类的使用上、getEnclosingClass能够获取匿名内部类对应的外部类Class对象，而getDeclaringClass不能够获取匿名内部类对应的声明类Class对象。
	 */
	public static void main(String[] args) {

		Class<?> enclosingTopClass = ClassTest.class.getEnclosingClass();
		System.out.println(enclosingTopClass);  // null

		Class<?> declaringTopClass = ClassTest.class.getDeclaringClass();
		System.out.println(declaringTopClass);  // null

		Class<?> enclosingClass = InnerClass.class.getEnclosingClass();
		System.out.println(enclosingClass.getName());  // com.test.context.ClassTest

		Class<?> declaringClass = InnerClass.class.getDeclaringClass();
		System.out.println(declaringClass.getName());  // com.test.context.ClassTest

		//注意：GetEnclosingClass获取的是直接定义该类的外部类Class实例、这点和getDeclaringClass一致
		Class<?> enclosingClass1 = InnerClass.InnerInnerClass.class.getEnclosingClass();
		System.out.println(enclosingClass1.getName());  // com.test.context.ClassTest$InnerClass

		Class<?> declaringClass1 = InnerClass.InnerInnerClass.class.getDeclaringClass();
		System.out.println(declaringClass1.getName());    // com.test.context.ClassTest$InnerClass

		// 针对匿名内部类的测试
		DifferentBetweenClassGetEnclosingClassAndDeclaringClass s = new DifferentBetweenClassGetEnclosingClassAndDeclaringClass();
		HelloService inst = s.getHelloService();
		inst.sayHello();
	}

	private static class InnerClass {
		private static class InnerInnerClass {
		}
	}


	public interface HelloService {
		void sayHello();
	}

	public static class DifferentBetweenClassGetEnclosingClassAndDeclaringClass {
		HelloService getHelloService() {
			// 匿名内部类
			return new HelloService() {
				@Override
				public void sayHello() {
					System.out.println(this.getClass().getEnclosingClass());  // class com.test.context.ClassTest$DifferentBetweenClassGetEnclosingClassAndDeclaringClass
					System.out.println(this.getClass().getDeclaringClass());  // null
				}
			};
		}

	}
}