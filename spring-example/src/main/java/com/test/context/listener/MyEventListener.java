package com.test.context.listener;

import com.test.context.event.ABeanInitEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j(topic = "e")
@Component
public class MyEventListener {

	@EventListener(classes = {ApplicationEvent.class})
	public void listenApplicationEvent(ApplicationEvent event) {
		log.debug("application event：" + event.getClass().getName());
	}

	@Order(1)
	@EventListener(classes = ABeanInitEvent.class)
	public void listenABeanInitEvent(ABeanInitEvent event) {
		log.debug("listen event：" + event.getClass().getName());
	}

	@Order(2)
	@EventListener
	public void listenABeanInitEvent2(ABeanInitEvent event) {
		log.debug("listen event2：" + event.getClass().getName());
	}

	// 监听所有事件
	@Order(3)
	@EventListener(classes = {Object.class})
	public void listenABeanInitEvent3(Object event) {
		log.debug("listen event3：" + event.getClass().getName());
	}
}
