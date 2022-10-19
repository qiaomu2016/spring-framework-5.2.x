package com.example.beans;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j(topic = "e")
@Component
public class B {
	public B(){
		log.debug("create b");
	}
}
