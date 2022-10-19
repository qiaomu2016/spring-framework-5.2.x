package org.spring.introspect.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j(topic = "e")
@Component("a")
public class SpringBeanInfoTest {
	@Autowired
	AnnotationAutowiredFiledBeanTest autowiredFiledBeanTest;


	@Autowired
	public void setAnnotationAutowiredMethodBeanTest(AnnotationAutowiredMethodBeanTest annotationAutowiredMethodBeanTest){
		log.debug("AnnotationAutowiredMethodBeanTest=[{}]",annotationAutowiredMethodBeanTest);
	}

	public void setAutowiredInjectByTypeMethodBeanTest(AutowiredInjectByTypeMethodBeanTest autowiredInjectByTypeMethodBeanTest){
		log.debug("AutowiredInjectByTypeMethodBeanTest=[{}]",autowiredInjectByTypeMethodBeanTest);
	}

	public void setBeanDefinitionPropertyValuesBeanTest(BeanDefinitionPropertyValuesBeanTest beanDefinitionPropertyValuesBeanTest){
		log.debug("BeanDefinitionPropertyValuesBeanTest=[{}]",beanDefinitionPropertyValuesBeanTest);
	}

	//spring 注入
//	getC(){
//
//	}

	public void printf(){
		log.debug("autowiredFiledBeanTest=[{}]",autowiredFiledBeanTest);
	}

}
