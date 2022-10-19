package com.test.context.listener;

import com.test.context.event.ABeanInitEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j(topic = "e")
@Component
public class MyEventListener {

	@EventListener(classes = {ApplicationEvent.class})
	public void listenApplicationEvent(ApplicationEvent event) {
		log.debug("application event：" + event.getClass().getName());
	}

	@EventListener(classes = {ABeanInitEvent.class})
	public void listenABeanInitEvent(ABeanInitEvent event) {
		log.debug("bean event：" + event.getClass().getName());
	}
}
