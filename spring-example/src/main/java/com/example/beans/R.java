package com.example.beans;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@PropertySource("classpath:application.properties")
@Slf4j(topic = "e")
public class R {
	@Value("${k1}")
	String k1;

	@PostConstruct
	public void init(){
		log.debug("k1:[{}]",k1);
	}
}
