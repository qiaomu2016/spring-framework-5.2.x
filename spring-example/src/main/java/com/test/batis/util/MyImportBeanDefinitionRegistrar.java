package com.test.batis.util;

import com.test.batis.dao.AMapper;
import com.test.batis.dao.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.HashMap;
import java.util.Map;

/**
 * 如何动态注册BeanDefinition
 * 实现BeanFactoryPostProcessor其实是可以完成BeanDefinition注入的,但是不建议在BeanFactoryPostProcessor去注册BeanDefinition；原因后面再讲；
 * 所以这里姑且我们认为BeanFactoryPostProcessor是无法动态注册BeanDefinition的；只得另选方案；
 * spring还有一个接口可以实现BeanDefinition的注册——BeanDefinitionRegistryPostProcessor
 * 实现BeanDefinitionRegistryPostProcessor重写postProcessBeanDefinitionRegistry也可以完成BeanDefinition的注册；但是我们这里不选这个方案；原因有两点
 * 1、因为我熟悉mybatis的源码，mybatis当中用的就不是这个，所以我也不用
 * 2、比起BeanDefinitionRegistryPostProcessor还有一个更优秀的方案——ImportBeanDefinitionRegistrar
 * 故而我们这里选择扩展ImportBeanDefinitionRegistrar，至于他们有什么区别，后面再讲
 */
@Slf4j(topic = "e")
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

	Map<String, BeanDefinition> map = new HashMap<>();

	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		scan(); // 模拟mybatis，目前是写死的
		for (String key : map.keySet()) {
			AbstractBeanDefinition mapperBd = (AbstractBeanDefinition) map.get(key);
			Class<?> mapperClz = mapperBd.getBeanClass();
			log.debug("before:{}", mapperBd.getBeanClass().getName());
			mapperBd.setBeanClass(MyFactoryBean.class);
			log.debug("after:{}", mapperBd.getBeanClass().getName());
			mapperBd.getPropertyValues().add("mapperInterface", mapperClz.getName());
			registry.registerBeanDefinition(key, mapperBd);
		}
	}

	/**
	 * scan模拟了扫描（目前写死，后面会写全），把扫描结果放到了map当中；遍历map，把map当中的BeanDefinition对应的beanClass替换成为MyFactoryBean
	 * （因为我们的目的就是为了让MyFactoryBean生效）；然后又给BeanDefinition填充了getPropertyValues()的值为当前类（UserMapper）,故而后面spring源码在实例化这些MyFactoryBean的时候会去setter当前类
	 * （UserMapper）；从而完成代理；这里用了编码的方式写了一个for循环完成了多个mapper的注入；至此，我们完全参考mybatis的代码完成了自己写的山寨版和spring的完美整合、扩展；
	 *
	 * 值得说明的是我们参考的实际上是老版本的mybatis-spring的源码，新版的扩展有一点点不同；那么新版mybatis-spring的扩展原理和老版本的区别在哪里呢？包括上面所有的扩展问题的原理是什么？
	 */
	public void scan() {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(UserMapper.class);
		map.put("userMapper", builder.getBeanDefinition());
		builder = BeanDefinitionBuilder.genericBeanDefinition(AMapper.class);
		map.put("aMapper", builder.getBeanDefinition());
	}

}
