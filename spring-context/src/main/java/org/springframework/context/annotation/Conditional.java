/*
 * Copyright 2002-2020 the original author or authors.
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

package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a component is only eligible for registration when all
 * {@linkplain #value specified conditions} match.
 *
 * <p>A <em>condition</em> is any state that can be determined programmatically
 * before the bean definition is due to be registered (see {@link Condition} for details).
 *
 * <p>The {@code @Conditional} annotation may be used in any of the following ways:
 * <ul>
 * <li>as a type-level annotation on any class directly or indirectly annotated with
 * {@code @Component}, including {@link Configuration @Configuration} classes</li>
 * <li>as a meta-annotation, for the purpose of composing custom stereotype
 * annotations</li>
 * <li>as a method-level annotation on any {@link Bean @Bean} method</li>
 * </ul>
 *
 * <p>If a {@code @Configuration} class is marked with {@code @Conditional},
 * all of the {@code @Bean} methods, {@link Import @Import} annotations, and
 * {@link ComponentScan @ComponentScan} annotations associated with that
 * class will be subject to the conditions.
 *
 * <p><strong>NOTE</strong>: Inheritance of {@code @Conditional} annotations
 * is not supported; any conditions from superclasses or from overridden
 * methods will not be considered. In order to enforce these semantics,
 * {@code @Conditional} itself is not declared as
 * {@link java.lang.annotation.Inherited @Inherited}; furthermore, any
 * custom <em>composed annotation</em> that is meta-annotated with
 * {@code @Conditional} must not be declared as {@code @Inherited}.
 *
 * @author Phillip Webb
 * @author Sam Brannen
 * @see Condition
 * @since 4.0
 */
// 如果是类和方法都加了@Conditional注解，最终在方法上的注解为最终的条件，如果返回true则加入容器，反之不会加入容器。
// 如果只是类上加了@Conditional注解，整个类的所有方法都会加入容器中。
@Target({ElementType.TYPE, ElementType.METHOD}) // 可以作用在方法上，也可以作用在类上。
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Conditional {

	/**
	 * 需要传入一个class数组，并且继承Condition接口
	 * Condition是个接口，需要实现matches方法，返回true则注入bean，false则不注入
	 * All {@link Condition} classes that must {@linkplain Condition#matches match}
	 * in order for the component to be registered.
	 */
	Class<? extends Condition>[] value();

//	springboot对@Conditional的扩展
//	@Conditional是springboot实现自动配置的关键基础能力。在此基础上，springboot又创建了多个适用于不同场景的组合条件注解。
//	@ConditionalOnBean：当上下文中有指定Bean的条件下进行实例化。
//	@ConditionalOnMissingBean：当上下文没有指定Bean的条件下进行实例化。
//	@ConditionalOnClass：当classpath类路径下有指定类的条件下进行实例化。
//	@ConditionalOnMissingClass：当类路径下没有指定类的条件下进行实例化。
//	@ConditionalOnWebApplication：当项目本身是一个Web项目时进行实例化。
//	@ConditionalOnNotWebApplication：当项目本身不是一个Web项目时进行实例化。
//	@ConditionalOnProperty：当指定的属性有指定的值时进行实例化。
//	@ConditionalOnExpression：基于SpEL表达式的条件判断。
//	@ConditionalOnJava：当JVM版本为指定的版本范围时进行实例化。
//	@ConditionalOnResource：当类路径下有指定的资源时进行实例化。
//	@ConditionalOnJndi：在JNDI存在时触发实例化。
//	@ConditionalOnSingleCandidate：当指定的Bean在容器中只有一个，或者有多个但是指定了首选的Bean时触发实例化。

}
