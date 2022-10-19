package com.example.beans;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

@Slf4j(topic = "e")
public class V  {
	public V(){
		log.debug("create v");
	}

	@Bean
	public  T t(){
		return new T();
	}
}
