package com.example.beans.importBeanDefinitionRegistrar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;

@Slf4j(topic = "e")
@Import(E.class)
public class CC {

	public CC(){
		log.debug("create cc");
	}

}
