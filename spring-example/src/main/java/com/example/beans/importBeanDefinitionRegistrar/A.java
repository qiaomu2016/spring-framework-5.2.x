package com.example.beans.importBeanDefinitionRegistrar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Slf4j(topic = "e")
@Component
@Import(D.class)
public class A {

	public A(){
		log.debug("create a");
	}

}
