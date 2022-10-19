package com.example.beans;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

@Slf4j(topic = "e")
public class N {
	public N(){
		log.debug("create n");
	}

	@Bean
	public P p(){
		return new P();
	}
}
