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

/**
 * A {@link Condition} that offers more fine-grained control when used with
 * {@code @Configuration}. Allows certain conditions to adapt when they match
 * based on the configuration phase. For example, a condition that checks if a bean
 * has already been registered might choose to only be evaluated during the
 * {@link ConfigurationPhase#REGISTER_BEAN REGISTER_BEAN} {@link ConfigurationPhase}.
 *
 * @author Phillip Webb
 * @since 4.0
 * @see Configuration
 */
public interface ConfigurationCondition extends Condition {

	/**
	 * Return the {@link ConfigurationPhase} in which the condition should be evaluated.
	 */
	ConfigurationPhase getConfigurationPhase();

	/*
	 * 为什么要分为两个阶段？
	 * 按照常理来说，解析就是加载，因为解析完了，下一步直接就加载了，没什么问题。但是对于Spring的Configuration来说，不是这个样子的。
	 * 对于配置类来说，配置类里面是是可以导入普通的类，也是可以导入一个配置类，这就分为两个阶段了。在读取之后，需要加载。
	 * 读取@import，@Bean，@ComponentScan，等这种可以向容器中添加bean的注解。
	 * 加载，这里的加载是要解析那些普通的注解，比如，@role。@Scope，还有bean的那些init和destroy方法，等等。这些注解不会在Spring中添加具体的bean信息。
	 *
	 * 基于上面的原因，所以在分为了两个阶段。分成两个节点，就可以更加细粒的控制，比如对于一个配置类来说，getConfigurationPhase返回值是REGISTER_BEAN。
	 * 那么在解析Configuration的时候，就会将这个bean添加进去。并不会调用它的matches方法，也就是在这个阶段不用管Match方法的结果。
	 *
	 * PARSE_CONFIGURATION阶段
	 * 在开始解析Configuration标注的类。这个阶段主要是解析@import，@Bean，@ComponentScan。
	 * 主要是在ConfigurationClassParser#parse方法里面
	 *
	 * REGISTER_BEAN阶段
	 * 在上面解析完之后，这个阶段会解析@role，这种不会向Spring中添加bean的注解，构建BeanDefinition，添加到BeanFactory中，这个时候才会真正的添加进来。
	 * 主要是在ConfigurationClassBeanDefinitionReader#loadBeanDefinitions方法里面
	 */


	/**
	 * The various configuration phases where the condition could be evaluated.
	 */
	enum ConfigurationPhase {

		/**
		 * 当配置类被解析的时候才会判断验证，如果此时条件不匹配，则不会添加@Configuration类
		 * The {@link Condition} should be evaluated as a {@code @Configuration}
		 * class is being parsed.
		 * <p>If the condition does not match at this point, the {@code @Configuration}
		 * class will not be added.
		 */
		PARSE_CONFIGURATION,

		/** 当添加一个普通bean时（非配置类）才会判断验证，该条件不会阻止添加@Configuration类
		 * 在条件满足时，表示所有的@Configuration类已经被解析。
		 * The {@link Condition} should be evaluated when adding a regular
		 * (non {@code @Configuration}) bean. The condition will not prevent
		 * {@code @Configuration} classes from being added.
		 * <p>At the time that the condition is evaluated, all {@code @Configuration}
		 * classes will have been parsed.
		 */
		REGISTER_BEAN
	}

}
