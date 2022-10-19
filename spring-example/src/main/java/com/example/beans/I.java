package com.example.beans;

import org.springframework.context.annotation.Bean;

public interface I {

	@Bean
	public default S s(){
		return new S();
	}
}
