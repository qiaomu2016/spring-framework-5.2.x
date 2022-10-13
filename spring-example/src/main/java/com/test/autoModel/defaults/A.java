package com.test.autoModel.defaults;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = "e")
public class A implements ExampleAware {

	ExampleBean exampleBean;

	public A() {
		log.debug("default Constructor");
	}

	@Override
	public void setExampleBean(ExampleBean exampleBean) {
		this.exampleBean = exampleBean;
	}

	public ExampleBean getExampleBean() {
		return exampleBean;
	}
}
