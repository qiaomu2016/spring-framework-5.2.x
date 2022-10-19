/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context;

import java.util.EventListener;

/**
 * Interface to be implemented by application event listeners.
 *
 * <p>Based on the standard {@code java.util.EventListener} interface
 * for the Observer design pattern.
 *
 * <p>As of Spring 3.0, an {@code ApplicationListener} can generically declare
 * the event type that it is interested in. When registered with a Spring
 * {@code ApplicationContext}, events will be filtered accordingly, with the
 * listener getting invoked for matching event objects only.
 *
 * ApplicationListener是一个泛型接口，泛型的类型必须是 ApplicationEvent 及其子类，只要实现了这个接口，那么当容器有相应的事件触发时，就能触发 onApplicationEvent 方法
 *
 * 对于 Spring 容器的一些事件，可以监听并且触发相应的方法。通常的方法有 2 种，ApplicationListener 接口和@EventListener 注解。
 * 要想顺利的创建监听器，并起作用，这个过程中需要这样几个角色：
 * 1、事件（event）可以封装和传递监听器中要处理的参数，如对象或字符串，并作为监听器中监听的目标。
 * 		定义事件：MyTestEvent extends ApplicationEvent
 * 2、监听器（listener）具体根据事件发生的业务处理模块，这里可以接收处理事件中封装的对象或字符串。
 * 		定义监听器：MyApplicationListener implements ApplicationListener<MyTestEvent>
 * 3、事件发布者（publisher）事件发生的触发者。
 * 		事件发布：在类里自动注入了ApplicationEventPublisher，通过调用#publishEvent(ApplicationEvent)方法发布事件，applicationEventPublisher.publishEvent(new MyTestEvent(this, msg))
 *
 * {@link EventListener} 注解
 * 除了通过实现接口，还可以使用@EventListener 注解，实现对任意的方法都能监听事件。
 * 在任意方法上标注@EventListener 注解，指定 classes，即需要处理的事件类型，一般就是 ApplicationEvent 及其子类，可以设置多项。
 * @code { @EventListener(classes = {MyTestEvent.class}) } ，其实添加@EventListener注解的方法会被包装成了ApplicationListener对象
 *
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @param <E> the specific {@code ApplicationEvent} subclass to listen to
 * @see org.springframework.context.ApplicationEvent
 * @see org.springframework.context.event.ApplicationEventMulticaster
 * @see org.springframework.context.event.EventListener
 */
@FunctionalInterface
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {

	/**
	 * Handle an application event.
	 * @param event the event to respond to
	 */
	void onApplicationEvent(E event);

}
