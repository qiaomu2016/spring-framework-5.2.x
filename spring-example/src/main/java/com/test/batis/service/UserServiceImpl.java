package com.test.batis.service;

import com.test.batis.dao.AMapper;
import com.test.batis.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

	/**
	 * UserMapper 对象怎么放到spring容器
	 * 假设这个模拟的mybatis功能 MySqlSession 已经很完善了；但是还是无法和spring工作；因为我们返回的代理对象不在spring容器当中；无法在一个bean当中被依赖注入进来；
	 *
	 * 比如userMapper属性是无法被注入成功的；于是你得思考如何把一个自己实例化的对象（可能是new，可能是序列化，可能是代理，总之是一个第三方的对象）给到spring容器？
	 * 1.@Component注解方式——这种方式是把类给spring管理
	 * 2.——同理这种方式也是把类给spring管理
	 * 3.动态注册beanDefinition——这种方式是把beanDefinition对象给spring，并不是自己产的真实对象
	 *
	 * 以上三种方式会被很多人误解可以把一个对象给到spring容器；当然这三种方式最后也是产生了一个对象，并且存到了容器当中；
	 * 但是问题的关键是这个对象的产生过程是我们无法去控制的；是由spring容器去实例化的对象；比如userMapper对象，是需要我们用动态代理这种特定方式来产生，spring肯定不会帮你完成；
	 * 考虑下面的三种方式：
	 * 1.@Bean方式——通过在方法当中编码，可以自己产生对象；返回后即存在容器，符合要求
	 * 2.FactoryBean方式——通过实现FactoryBean接口也可以自己编码产生对象，符合要求
	 * 3.beanFactory.registerSingleton(String name,Object object)——直接传自己的对象 符合要求
	 * FactoryBean是一个特殊接口，实现了factoryBean的接口通常会产生两个bean，第一个实现类本身的对象，第二个是getObject方法当中返回的对象；
	 *
	 * mybatis是如何选择的？
	 * 查阅mybatis的文档，他选择的是factoryBean这种方式完成了代理对象的bean的注册；
	 * <bean id="userMapper" class="org.mybatis.spring.mapper.MapperFactoryBean">
	 * 		<property name="mapperInterface" value="org.mybatis.spring.sample.mapper.UserMapper" />
	 * 		<property name="sqlSessionFactory" ref="sqlSessionFactory" />
	 * </bean>
	 * mybatis开发了一个MapperFactoryBean，在他的getObject方法当中产生了代理对象；代理对象实现接口通过属性注入进去，即上面配置文件中的UserMapper
	 *
	 * 精简后的MapperFactoryBean源码：
	 * public class MapperFactoryBean implements FactoryBean{
	 * private Class mapperInterface;
	 * //返回代理对象
	 * public T getObject() throws Exception {
	 * 		return getSqlSession().getMapper(this.mapperInterface);
	 * }
	 * //xml配置的时候，提供Mapper，实现代理
	 * public void setMapperInterface(Class<T> mapperInterface) {
	 * 		this.mapperInterface = mapperInterface;
	 * }
	 * }
	 * 于是我们也开发一个MyFactoryBean来模拟MapperFactoryBean
	 *
	 * 最后我们在xml当中配置，也能完成UserMapper的注入，我们写的山寨版mybatis也能正常跑；
	 * 但是如果应用当中存在多个mapper，那么需要在xml当中配置多个MyFactoryBean；显然这不科学，所以需要实现多个mapper的注入；
	 * 实现多个mapper的注入和单个mapper的注入都离不开前面讨论的如何把对象叫给spring容器；区别在于多个对象和单个对象而以；所以还是在上面三个方案当中选；
	 *
	 * 稍微思考都可以排除@Bean方式和beanFactory.registerSingleton方式；排除所有的不可能剩下的那个就一定是FactoryBean，
	 * 于是FactoryBean方式是唯一可能实现多个mapper注入的方式；开始思考下面问题
	 * 1、使用FactoryBean的第一个要求便是要实现FactoryBean的类，比如这自定义的MyFactoryBean，另外必须存在spring容器当中
	 * 2、只有MyFactoryBean生效才能考虑实现多个注入
	 * 首先让MyFactoryBean生效的方式我们上面使用的是xml；但是我们也知道xml只能实现单个mapper的注入；故而排除XML让MyFactoryBean生效的方式；
	 * 第二种就是给MyFactoryBean加@Component注解；首先这种方式mybatis不可能使用——需要程序员去扫描；其次@Component方式连一个mapper也无法注入；
	 * 剩下最后一种方式动态注册beanDefinition方式；
	 */

	@Autowired
	UserMapper userMapper;

	@Autowired
	AMapper aMapper;

	public void setUserMapper(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	public void setAMapper(AMapper aMapper) {
		this.aMapper = aMapper;
	}

	@Override
	public List<Map<String, Object>> queryUserList() {
		return userMapper.queryList();
	}


	@Override
	public Map<String, Object> queryFroMap(Integer id) {
		return userMapper.queryForMap(id);
	}
}
