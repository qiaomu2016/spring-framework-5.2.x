package com.example.beans;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "e")
public class D extends V implements I{

	public D(){
		log.debug("create d");
	}
}
