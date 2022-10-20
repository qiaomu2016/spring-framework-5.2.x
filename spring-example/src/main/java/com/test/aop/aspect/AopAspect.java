package com.test.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * 什么是aop?
 * Aspect Oriented Programming 面向切面编程，在传统的oop开发过程当中，逻辑是自上而下的，譬如我们实现一个登录功能，浏览器发起http请求到controller，
 * controller负责接受请求，封装参数、验证参数等等，继而把数据传给service，service处理完成之后调用dao，dao操作db后返回；这个过程当中会产生很多横切性的问题；
 * 比如controller当中的日志记录、比如service当中的权限校验，比如dao当中的事务处理；这些横切性的问题和主业务逻辑是没有关系的，比如日志的记录失败与否并不会影响整个登录逻辑；
 * 这些横切的问题如果不去处理会散落在代码的各个地方；面向切面编程就是我们程序员编码只关心横切性问题的抽象；
 *
 * 什么spring aop
 * aop 面向切面编程是一种编程目标，说白了是一个标准；而实现这种编程标准的技术手段有很多；比如你可以用JDK动态代理来实现aop，也可以使用aspectj这种静态编程技术来实现代理从而实现aop；
 * spring aop就是实现aop 的一种技术手段；
 *
 * spring aop的核心原理
 * 对于上述的横切性问题spring的做法是把这些问题集中到一个切面（Aspect）当中，然后利用动态代理技术动态增强业务bean；
 * spring aop的核心技术是JDK动态代理和cglib动态代理，加上spring内部的BeanPostProcessor一起工作，从而达到了对spring当中bean完成增强
 *
 * spring aop当中的专业术语：
 * Join point 连接点：程序执行当中的一个点，他是一个最终结果，因为spring aop最小连接单位是一个方法，故而一个切点就是一个方法（但是是被连接了的方法）
 * Pointcut 切点：一组连接点的集合；因为在开发中可能连接点可能会有很多，根绝业务和功能的不同会进行分组，切点就是一组连接点的描述（
 * 			相当于连接点=一条数据，切点等于一张表；一个数据库当中可以存在多张表，表可以有多条数据）
 * Advice通知：通知可以从三个维度来理解
 * 	 1、通知的内容，也就是你增强一个方法的具体业务逻辑，比如日志记录，比如事务操作；
 * 	 2、通知的时机：Before advice、After returning advice、After throwing advice、After (finally) advice、Around advice；
 * 	 3、通知的目标，也就是这个通知需要作用到那个或者哪些连接点上
 * Introduction 导入：springAop可以在不修改代码的情况下让某个类强制实现某个接口，并且可以指定接口的默认实现方法；
 * Target object 目标对象：由于spring aop是借助CGLIB或者JDK动态代理的来实现增强的，目标对象的概念和动态代理当中那个目标对象是同一个概念——被代理的那个对象
 * AOP proxy 代理对象：一个被jdk动态代理或者被CGLIB动态代理了之后的对象
 * Weaving 织入：一个过程；把切面和应用程序对象连接的过程
 * Aspect 切面：上述所有概念在编码过程中存在的类称为切面，切点、连接点、通知等等存在的类称之为一个切面
 *
 * spring aop开发
 * 1、启用@AspectJ
 * 支持使用Java Configuration启用@AspectJ支持要使用Java @Configuration启用@AspectJ支持，请添加@EnableAspectJAutoProxy注释
 * <code>
 * 	@Configuration
 * 	@EnableAspectJAutoProxy
 * 	public class AppConfig {
 * 	}
 * </code>
 * 使用XML配置启用@AspectJ支持要使用基于xml的配置启用@AspectJ支持，可以使用aop:aspectjautoproxy元素：<aop:aspectj-autoproxy/>
 * 2、声明一个Aspect
 * 申明一个@Aspect注释类，并且定义成一个bean交给Spring管理
 * <code>
 * 	@Component
 * 	@Aspect
 * 	public class UserAspect {
 * 	}
 * </code>
 * 3、申明一个pointCut
 * 切入点表达式由@Pointcut注释表示。切入点声明由两部分组成:一个签名包含名称和任何参数，以及一个切入点表达式，该表达式确定我们对哪个方法执行感兴趣
 * <code>
 * 	@Pointcut("execution(* transfer(..))")// 切入点表达式
 * 	private void anyOldTransfer() {}// 切入点签名
 * </code>
 * 切入点确定感兴趣的 join points（连接点），从而使我们能够控制何时执行通知。Spring AOP只支持Spring bean的方法执行 join points（连接点），
 * 所以您可以将切入点看作是匹配Spring bean上方法的执行
 * <code>
 * // 申明Aspect，并且交给spring容器管理
 * 	@Component
 * 	@Aspect public class UserAspect {
 * 	// 申明切入点，匹配UserDao所有方法调用
 * 	// execution匹配方法执行连接点
 * 	// within:将匹配限制为特定类型中的连接点
 * 	// args：参数
 * 	// target：目标对象
 * 	// this：代理对象
 * 	@Pointcut("execution(* com.yao.dao.UserDao.*(..))")
 * 	public void pintCut(){
 * 	System.out.println("point cut");
 * 	}
 * </code>
 * 4、申明一个Advice通知
 * advice通知与pointcut切入点表达式相关联，并在切入点匹配的方法执行@Before之前、@After之后或前后运行
 * <code>
 *  // 申明Aspect，并且交给spring容器管理
 * 	@Component
 * 	@Aspect
 * 	public class UserAspect {
 * 		// 申明切入点，匹配UserDao所有方法调用
* 	    @Pointcut("execution(* com.yao.dao.UserDao.*(..))")
 * 		public void pintCut(){
 * 			System.out.println("point cut");
 * 	    }
 * 		// 申明before通知,在pintCut切入点前执行
 * 		// 通知与切入点表达式相关联，并在切入点匹配的方法执行之前、之后或前后运行。
 * 		// 切入点表达式可以是对指定切入点的简单引用，也可以是在适当位置声明的切入点表达式。
 * 	   @Before("com.yao.aop.UserAspect.pintCut()")
 * 	   public void beforeAdvice(){
 * 			System.out.println("before");
 * 	   }
 * 	}
 * </code>
 *
 */

@Aspect
@Component
@Slf4j(topic = "e")
public class AopAspect {

	/**
	 * 【execution】
	 * 用于匹配方法执行 join points连接点，最小粒度方法，在aop中主要使用
	 * execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?namepattern(param-pattern) throws-pattern?)
	 * 这里问号表示当前项可以有也可以没有，其中各项的语义如下
	 * modifiers-pattern：方法的可见性，如public，protected；
	 * ret-type-pattern：方法的返回值类型，如int，void等；
	 * declaring-type-pattern：方法所在类的全路径名，如com.spring.Aspect；
	 * name-pattern：方法名，如buisinessService()；
	 * param-pattern：方法的参数类型，如java.lang.String；
	 * throws-pattern：方法抛出的异常类型，如java.lang.Exception；
	 *
	 * example:
	 * @Pointcut("execution(* com.chenss.dao.*.*(..))") //匹配com.chenss.dao包下任意接口和类的任意方法
	 * @Pointcut("execution(public * com.chenss.dao.*.*(..))") //匹配com.chenss.dao包下的任意接口和类的public方法
	 * @Pointcut("execution(public * com.chenss.dao.*.*())") //匹配com.chenss.dao包下的任意接口和类的public 无方法参数的方法
	 * @Pointcut("execution(* com.chenss.dao.*.*(java.lang.String, ..))") //匹配com.chenss.dao包下的任意接口和类的第一个参数为String类型的方法
	 * @Pointcut("execution(* com.chenss.dao.*.*(java.lang.String))") //匹配com.chenss.dao包下的任意接口和类的只有一个参数，且参数为String类型的方法
	 * @Pointcut("execution(* com.chenss.dao.*.*(java.lang.String))") //匹配com.chenss.dao包下的任意接口和类的只有一个参数，且参数为String类型的方法
	 * @Pointcut("execution(public * *(..))") //匹配任意的public方法 @Pointcut("execution(* te*(..))")//匹配任意的以te开头的方法
	 * @Pointcut("execution(* com.chenss.dao.IndexDao.*(..))") //匹配com.chenss.dao.IndexDao接口中任意的方法
	 * @Pointcut("execution(* com.chenss.dao..*.*(..))") //匹配com.chenss.dao包及其子包中任意的方法
	 * 关于这个表达式的详细写法：https://docs.spring.io/spring-framework/docs/current/spring-frameworkreference/core.html#aop-pointcuts-examples
	 *
	 * 由于Spring切面粒度最小是达到方法级别，而execution表达式可以用于明确指定方法返回类型，类名，方法名和参数名等与方法相关的信息，并且在Spring中，
	 * 大部分需要使用AOP的业务场景也只需要达到方法级别即可，因而execution表达式的使用是最为广泛的
	 *
	 * 【within】
	 * 该表达式的最小粒度为类
	 * within与execution相比，粒度更大，仅能实现到包和接口、类级别。而execution可以精确到方法的返回值，参数个数、修饰符、参数类型等
	 * @Pointcut("within(com.chenss.dao.*)") //匹配com.chenss.dao包中的任意方法
	 * @Pointcut("within(com.chenss.dao..*)") //匹配com.chenss.dao包及其子包中的任意方法
	 *
	 * 【args】
	 * args表达式的作用是匹配指定参数类型和指定参数数量的方法,与包名和类名无关args同execution不同的地方在于：
	 * args匹配的是运行时传递给方法的参数类型 execution(* *(java.io.Serializable))匹配的是方法在声明时指定的方法参数类型
	 *
	 * 【this】
	 * JDK代理时，指向接口和代理类proxy，cglib代理时 指向接口和子类(不使用proxy)
	 *
	 * 【target】
	 * target 指向接口和子类
	 * 如果配置设置proxyTargetClass=false，或默认为false，则是用JDK代理，否则使用的是CGLIB代理。
	 * JDK代理的实现方式是基于接口实现，代理类继承Proxy，实现接口。而CGLIB继承被代理的类来实现。
	 * 所以使用target会保证目标不变，关联对象不会受到这个设置的影响。但是使用this对象时，会根据该选项的设置，判断是否能找到对象。
	 * @Pointcut("target(com.chenss.dao.IndexDaoImpl)") //目标对象，也就是被代理的对象。限制目标对象为com.chenss.dao.IndexDaoImpl类
	 * @Pointcut("this(com.chenss.dao.IndexDaoImpl)") //当前对象，也就是代理对象，代理对象时通过代理目标对象的方式获取新的对象，与原值并非一个
	 * @Pointcut("@target(com.chenss.anno.Chenss)") //具有@Chenss的目标对象中的任意方法
	 * @Pointcut("@within(com.chenss.anno.Chenss)") //等同于@target
	 *
	 */

	@Pointcut("execution(* com.test.aop.service.*.*(..))")
	public void pointCut() {

	}

	@Pointcut("execution(* com.test.transaction.*.*(..))")
	public void pointCutOut() {

	}

	@After("pointCut()")
	public void after() {
		log.debug("anno after");
	}

	@Before("pointCut()")
	public void before() {
		log.debug("anno before");
	}

	@AfterReturning("pointCut()")
	public void afterReturning() {
		log.debug("anno afterReturning");
	}


	@Before("pointCutOut()")
	public void testMethod() {
		log.debug("testMethod");
	}
}
