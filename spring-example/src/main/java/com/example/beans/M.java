package com.example.beans;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

/**
 * M 讲道理是一个半配置类
 * 不管是full还是litespring都会把他当成配置类
 * 配置类----bean来处理
 *
 * 从M的表现形式是一个lite但是spring不会解析他
 * 因为spring没有发觉他的存在
 */
@Slf4j(topic = "e")

public class M {
	public M(){
		log.debug("create m");
	}


	@Bean
	public N n(){
		return new N();
	}
}
