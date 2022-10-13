package com.test.autoModel.dependsOn;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component("da")
@Slf4j(topic = "e")
@DependsOn("db")  // @DependsOn注解的主要作用就是干扰bean的实例化顺序。就是在实例化DA这个bean之前先实例化名字叫“db”的bean；
// 需要说明的是@DependsOn("value")这里的value是bean的名字不是类名
public class DA {

	@PostConstruct
	public void initMethod(){
		log.debug("DA initMethod");
	}
}
