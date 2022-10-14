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
 * Enumerates the various scoped-proxy options.
 *
 * @Scope注解中的proxyMode方法值指示了IoC容器要不要为Bean创建代理，如何创建代理，是使用JDK的动态代理还是使用CGLIB？
 * ScopedProxyMode的DEFAULT和NO作用是一样的
 * 如果配置为INTERFACES或TARGET_CLASS，在ScopedProxyUtils的createScopedProxy方法中将会为目标Bean创建一个ScopedProxyFactoryBean的BeanDefinition，
 * 并使用目标Bean的beanName来注册这个BeanDefinition，将目标Bean的beanName拼接上"ScopedTarget."前缀来注册目标Bean的BeanDefinition。
 * 同时将目标BeanDefinition的autowireCandidate属性设置为false，以此来确保IoC容器在查找该类型的单个Bean时(getBean方法)不会返回原始Bean实例，而是返回经过代理后的Bean实例。
 *
 * <p>For a more complete discussion of exactly what a scoped proxy is, see the
 * section of the Spring reference documentation entitled '<em>Scoped beans as
 * dependencies</em>'.
 *
 * @author Mark Fisher
 * @since 2.5
 * @see ScopeMetadata
 */
public enum ScopedProxyMode {

	/**
	 * Default typically equals {@link #NO}, unless a different default
	 * has been configured at the component-scan instruction level.
	 */
	DEFAULT,

	/**
	 * Do not create a scoped proxy.
	 * <p>This proxy-mode is not typically useful when used with a
	 * non-singleton scoped instance, which should favor the use of the
	 * {@link #INTERFACES} or {@link #TARGET_CLASS} proxy-modes instead if it
	 * is to be used as a dependency.
	 */
	NO,

	/**
	 * Create a JDK dynamic proxy implementing <i>all</i> interfaces exposed by
	 * the class of the target object.
	 */
	INTERFACES,

	/**
	 * Create a class-based proxy (uses CGLIB).
	 */
	TARGET_CLASS

}
