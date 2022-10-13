package com.test.batis;

import com.test.batis.bean.X;
import com.test.batis.config.BatisConfig;
import com.test.batis.dao.UserMapper;
import com.test.batis.service.UserService;
import com.test.batis.mybatis.MySqlSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Test;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Slf4j(topic = "e")
public class BatisTest {

	/**
	 * 测试 单独使用mybatis
	 */
	@Test
	public void onlyBatis() {
		BatisConfig config = new BatisConfig();
		DataSource dataSource = config.dataSource();
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, dataSource);

		Configuration configuration = new Configuration(environment);
		configuration.addMapper(UserMapper.class);

		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		SqlSession sqlSession = sqlSessionFactory.openSession();

		// UserMapper实际是一个没有任何实现的接口；那么返回的对象怎么产生的呢？
		// 翻阅mybatis的源码可以得知（MapperProxyFactory），底层是使用了JDK动态代理的技术完成了对象的实例化
		UserMapper mapper = sqlSession.getMapper(UserMapper.class);
		Map<String, Object> resultMap = mapper.queryForMap(1); // 查询
		log.debug("resultMap:{}", resultMap);

		sqlSession.close();
	}


	/**
	 * 测试 spring整合mybatis
	 */
	@Test
	public void defaultBatis() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BatisConfig.class);
		UserService service = context.getBean(UserService.class);
		List<Map<String, Object>> resultList = service.queryUserList();
		log.debug("----------------------------------------------");
		log.debug("resultList:{}", resultList);
	}


	/**
	 * 测试山寨版的 mybatis
	 */
	@Test
	public void customBatis() {
		UserMapper mapper = (UserMapper) MySqlSession.getMapper(UserMapper.class);
		mapper.queryForMap(1);
		mapper.queryList();
	}

	/**
	 * 测试把一个第三方的对象给spring
	 * <p>
	 * 1、注解（@Service....） X
	 * 2、<bean id="n"></bean> X
	 * 3、注解（@Bean）
	 * 4、factoryBean
	 * 5、spring api
	 * 6、动态向容器注册BeanDefinition X
	 */
	@Test
	public void customObjectBatis() {
		// 一定要mybatis产生么？因为只有他自己知道要干什么事情
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BatisConfig.class);
		UserService service = context.getBean(UserService.class);
		service.queryUserList();
	}

	@Test
	public void beanDefinitionBatis() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.scan("com.test.batis.bean");
		context.refresh();
		context.getBean(X.class);
	}

	/**
	 * 测试 ImportBeanDefinitionRegistrar
	 */
	@Test
	public void importBeanDefinitionRegistrarBatis() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BatisConfig.class);

		UserService bean = context.getBean(UserService.class);
		bean.queryUserList();

		UserMapper tMapper = (UserMapper) context.getBean("userMapper");
		tMapper.queryList();
		context.getBean(X.class);
	}

}
