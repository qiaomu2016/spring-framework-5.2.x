package com.test.context.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

public class ABeanInitEvent extends ApplicationContextEvent {

	public ABeanInitEvent(ApplicationContext source) {
		super(source);
	}
}
