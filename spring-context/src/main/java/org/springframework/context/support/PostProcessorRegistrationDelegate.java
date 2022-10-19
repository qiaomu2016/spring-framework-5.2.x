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

package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.Nullable;

/**
 * Delegate for AbstractApplicationContext's post-processor handling.
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 4.0
 */
final class PostProcessorRegistrationDelegate {

	private PostProcessorRegistrationDelegate() {
	}


	/**
	 * 执行直接实现了 BeanFactoryPostProcessor 或者实现了 BeanDefinitionRegistryPostProcessor的类
	 *
	 * 问题：
	 * 1、顺序能不能变？ 意图？
	 * 	最好不要变；子类高于父类无可厚非，实现了排序的先执行也无可厚非；唯一有争议的是为什么API的先调用；
	 * 	spring是想尽量充分利用了postProcessBeanDefinitionRegistry方法的意义；避免注入不完整的bean；
	 * 	因为如果一个bean在扫描之后之后添加可能会有些功能不完善，api放到最前就不会有这个问题
	 * 2、BeanDefinitionRegistryPostProcessor 和 ImportBeanDefinitionRegistrar的区别
	 *  ImportBeanDefinitionRegistrar回调的时候可以获取注解信息
	 *  ImportBeanDefinitionRegistrar的执行时机早于BeanDefinitionRegistryPostProcessor（除api提供）
	 *  BeanDefinitionRegistryPostProcessor（除api提供）对一些bean的注册可能有些功能会失效比如@Bean
	 *  ImportBeanDefinitionRegistrar没有上述问题，因为他是在ConfigurationClassPostProcessor内部执行的
	 *  如果你一定要动态注册bd，尽量推荐试用ImportBeanDefinitionRegistrar
	 *  除非你除了要动态添加bd还需要对beanFactory做一些全局设定，那么可以用BeanDefinitionRegistryPostProcessor，因为他是一个bean工厂后置处理器
	 * 3、priorityOrderedPostProcessors为什么会先实例化 写法不一样
	 * 4、processedBeans当中为什么不存api传过来的   api提供的子类或者父类 都不可能重复执行
	 * 5、BeanDefinitionRegistryPostProcessor对于bd的修改，如何保证正确 提高当前BeanDefinitionRegistryPostProcessor的执行时机
	 * 6、BeanFactoryPostProcessor当中为什么不开放注册bd(其实是开放的) 有什么问题？-产生不合格的bean
	 *  我们不推荐用BeanFactoryPostProcessor来注册BeanDefinition
	 *  BeanFactoryPostProcessor的优先级比BeanDefinitionRegistryPostProcessor还要低，上面我们已经说过，不推荐使用BeanDefinitionRegistryPostProcessor，所以更加不推荐
	 *  BeanFactoryPostProcessor；因为你会可能会注册一个不完整功能的bean，除非你的bean你能确定没有那些特殊功能比如@Bean
	 * 7、如何确保postProcessBeanDefinitionRegistry方法对子类类型的bd的修改最大化生效
	 * 同级别子类修改一个子类的bd会失效（对普通类的修改不会失效），因为spring是找出同级别的bean直接实例化存到集合当中，然后一起执行，
	 * 这样在执行第一个的时候如果修改了第二个的bd则无效（这也不是bug，估计作者不想搞的太复杂）；但是对普通类的修改就不会失效了，因为普通类实例化特别
	 * 靠后，你修改一定会生效；如果你一定需要修改一个子类的bd，最好提高当前子类的级别
	 *
	 */
	public static void invokeBeanFactoryPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

		// Invoke BeanDefinitionRegistryPostProcessors first, if any.
		// 存放已经处理完的的BeanFactoryPostProcessor 以及他的子类 BeanDefinitionRegistryPostProcessor 的名字，防止重复执行
		Set<String> processedBeans = new HashSet<>();

		if (beanFactory instanceof BeanDefinitionRegistry) {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
			// 存放所有实现了 BeanFactoryPostProcessor的 bean
			List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
			// 存放所有实现了 BeanDefinitionRegistryPostProcessor的 bean
			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

			// ①：处理自定义的BeanFactoryPostProcessor
			// 迭代注册的 beanFactoryPostProcessors (注：为手动调用applicationContext.addBeanFactoryPostProcessor()添加的)
			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
				// 如果是 BeanDefinitionRegistryPostProcessor，则调用 postProcessBeanDefinitionRegistry 进行注册，同时加入到 registryProcessors 集合中
				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
					BeanDefinitionRegistryPostProcessor registryProcessor =
							(BeanDefinitionRegistryPostProcessor) postProcessor;
					registryProcessor.postProcessBeanDefinitionRegistry(registry);
					registryProcessors.add(registryProcessor);
				}
				else {
					// 否则当做普通的 BeanFactoryPostProcessor 处理，添加到 regularPostProcessors 集合中即可，便于后面做后续处理
					regularPostProcessors.add(postProcessor);
				}
			}

			// Do not initialize FactoryBeans here: We need to leave all regular beans
			// uninitialized to let the bean factory post-processors apply to them!
			// Separate between BeanDefinitionRegistryPostProcessors that implement
			// PriorityOrdered, Ordered, and the rest.
			// 用于保存当前处理的 BeanDefinitionRegistryPostProcessor
			// ②：这个currentRegistryProcessors 放的是Spring内部自已实现的BeanDefinitionRegistryPostProcessor接口的对象
			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

			// First, invoke the BeanDefinitionRegistryPostProcessors that implement PriorityOrdered.
			// 首先处理实现了 PriorityOrdered (优先排序接口)的 BeanDefinitionRegistryPostProcessor
			// 获取Spring框架之前注册的BeanDefinition,即:
			// org.springframework.context.annotation.internalConfigurationAnnotationProcessor ->  ConfigurationClassPostProcessor
			// 这个地方可以得到一个BeanDefinitionRegistryPostProcessor，因为是Spring默认在最开始自已注册的（即ConfigurationClassPostProcessor）
			// 为什么要在最开始注册这个呢？因为Spring的工厂需要做解析、扫描等功能，而这些功能都是需要在Spring工厂初始化完成之前执行，要么在工厂最开始的时候、要么在工厂初始化之中，反正不能在之后。
			String[] postProcessorNames =
					beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				// 判断是否实现了PriorityOrdered接口
				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) { // true
					// 调用beanFactory.getBean 实例化ConfigurationClassPostProcessor类， 并且放到当前需要执行的集合当中
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName); // 表示已经处理完了
				}
			}
			// 排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			// 合并加入registryProcessors集合，为什么要放到这个list当中？为了将来执行父类方法
			registryProcessors.addAll(currentRegistryProcessors);
			// 调用所有实现了 PriorityOrdered 的 BeanDefinitionRegistryPostProcessors 的 postProcessBeanDefinitionRegistry()
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			// 清空这个集合，因为下一次还要用
			currentRegistryProcessors.clear();

			// Next, invoke the BeanDefinitionRegistryPostProcessors that implement Ordered.
			// 其次，调用是实现了 Ordered（普通排序接口）的 BeanDefinitionRegistryPostProcessors，逻辑和 上面一样
			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
				}
			}
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			registryProcessors.addAll(currentRegistryProcessors);
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			currentRegistryProcessors.clear();

			// Finally, invoke all other BeanDefinitionRegistryPostProcessors until no further ones appear.
			// 最后调用其他的 BeanDefinitionRegistryPostProcessors
			boolean reiterate = true;
			while (reiterate) { // 默认是死循环
				reiterate = false; // 终止循环
				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
				for (String ppName : postProcessorNames) {
					if (!processedBeans.contains(ppName)) {
						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
						processedBeans.add(ppName);
						// 如果找到了，后面还需要继续循环，继续找
						// 找出来的必然是子类 有可能会动态添加新的 BeanDefinitionRegistryPostProcessor 的BeanDefinition
						reiterate = true;
					}
				}
				// 与上面处理逻辑一致
				sortPostProcessors(currentRegistryProcessors, beanFactory);
				registryProcessors.addAll(currentRegistryProcessors);
				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
				currentRegistryProcessors.clear();
			}

			// 【上面的逻辑是处理 BeanDefinitionRegistryPostProcessor的 postProcessBeanDefinitionRegistry()方法
			// 注：BeanDefinitionRegistryPostProcessor的父类BeanFactoryPostProcessor的回调方法是还没有执行的
			// 下面才开始处理 BeanFactoryPostProcessor的回调方法：postProcessBeanFactory()，包含了BeanDefinitionRegistryPostProcessor的父类方法】

			// Now, invoke the postProcessBeanFactory callback of all processors handled so far.
			// 调用所有 BeanDefinitionRegistryPostProcessor (包括手动注册和通过配置文件注册) 和 BeanFactoryPostProcessor(只有手动注册)的回调函数(postProcessBeanFactory())
			// 此处的 registryProcessors -> Spring自带的 ConfigurationClassPostProcessor
			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory); // 调用Spring自带或者通过API手动注册的BeanDefinitionRegistryPostProcessor的后处理方法postProcessBeanFactory
			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory); // 调用通过API手动注册的BeanFactoryPostProcessor的后处理方法postProcessBeanFactory
		}

		else {
			// Invoke factory processors registered with the context instance.
			// 如果不是 BeanDefinitionRegistry 只需要调用其回调函数（postProcessBeanFactory()）即可
			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
		}

		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let the bean factory post-processors apply to them!
		// 找父类
		String[] postProcessorNames =
				beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

		// Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		// 这里同样需要区分 PriorityOrdered 、Ordered 和 no Ordered
		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>(); // 实现了 priorityOrdered的父类集合
		List<String> orderedPostProcessorNames = new ArrayList<>(); // 实现了order接口的父类集合
		List<String> nonOrderedPostProcessorNames = new ArrayList<>(); // 其它
		for (String ppName : postProcessorNames) {
			if (processedBeans.contains(ppName)) {
				// skip - already processed in first phase above 已经处理过了的，跳过
			}
			else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) { // PriorityOrdered
				// 为什么这里先实例化？
				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) { // Ordered
				// 这里不实例化
				orderedPostProcessorNames.add(ppName);
			}
			else { // no Ordered
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
		// First, PriorityOrdered 接口
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

		// Next, invoke the BeanFactoryPostProcessors that implement Ordered.
		// Next, Ordered 接口
		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
		for (String postProcessorName : orderedPostProcessorNames) {
			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

		// Finally, invoke all other BeanFactoryPostProcessors.
		// Finally, no ordered
		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
		for (String postProcessorName : nonOrderedPostProcessorNames) {//A E
			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

		// Clear cached merged bean definitions since the post-processors might have
		// modified the original metadata, e.g. replacing placeholders in values...
		beanFactory.clearMetadataCache();
	}

	// 从Spring容器中找出的实现了BeanPostProcessor接口的Bean，并设置到BeanFactory中,之后bean被实例化的时候会调用这些BeanPostProcessor
	public static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {

		// 获取所有的 BeanPostProcessor 的 beanName，这些 beanName 都已经全部加载到容器中去，但是没有实例化
		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

		// Register BeanPostProcessorChecker that logs an info message when
		// a bean is created during BeanPostProcessor instantiation, i.e. when
		// a bean is not eligible for getting processed by all BeanPostProcessors.
		// 记录所有的beanProcessor数量
		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
		// 注册 BeanPostProcessorChecker，它主要是用于在 BeanPostProcessor 实例化期间记录日志
		// 当 Spring 中高配置的后置处理器还没有注册就已经开始了 bean 的实例化过程，这个时候便会打印 BeanPostProcessorChecker 中的内容
		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));

		// Separate between BeanPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>(); // 使用PriorityOrdered 保证顺序
		List<BeanPostProcessor> internalPostProcessors = new ArrayList<>(); // MergedBeanDefinitionPostProcessor
		List<String> orderedPostProcessorNames = new ArrayList<>();	// 使用 Ordered 保证顺序
		List<String> nonOrderedPostProcessorNames = new ArrayList<>(); // 没有顺序
		for (String ppName : postProcessorNames) {
			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class); // 调用 getBean 获取 bean 实例对象
				priorityOrderedPostProcessors.add(pp);
				if (pp instanceof MergedBeanDefinitionPostProcessor) {
					internalPostProcessors.add(pp);
				}
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, register the BeanPostProcessors that implement PriorityOrdered.
		// 第一步，注册所有实现了 PriorityOrdered 的 BeanPostProcessor
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory); // 先排序
		registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors); // 后注册

		// Next, register the BeanPostProcessors that implement Ordered.
		// 第二步，注册所有实现了 Ordered 的 BeanPostProcessor
		List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
		for (String ppName : orderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			orderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		sortPostProcessors(orderedPostProcessors, beanFactory); // 先排序
		registerBeanPostProcessors(beanFactory, orderedPostProcessors); // 后注册

		// Now, register all regular BeanPostProcessors.
		// 第三步注册所有无序的 BeanPostProcessor
		List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
		for (String ppName : nonOrderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			nonOrderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors); // 注册，无需排序

		// Finally, re-register all internal BeanPostProcessors.
		// 最后，注册所有的 MergedBeanDefinitionPostProcessor 类型的 BeanPostProcessor
		sortPostProcessors(internalPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, internalPostProcessors);

		// Re-register post-processor for detecting inner beans as ApplicationListeners,
		// moving it to the end of the processor chain (for picking up proxies etc).
		// 加入ApplicationListenerDetector（探测器），重新注册 BeanPostProcessor 以检测内部 bean，因为 ApplicationListeners 将其移动到处理器链的末尾
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
	}

	private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
		// 如果 beanFactory 为 DefaultListableBeanFactory ，则返回 BeanFactory 所依赖的比较器，
		// 否则返回默认的比较器(OrderComparator)，然后调用 List#sort(Comparator<? super E> c) 方法即可
		// Nothing to sort?
		if (postProcessors.size() <= 1) {
			return;
		}
		Comparator<Object> comparatorToUse = null;
		if (beanFactory instanceof DefaultListableBeanFactory) {
			comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
		}
		if (comparatorToUse == null) {
			comparatorToUse = OrderComparator.INSTANCE;
		}
		postProcessors.sort(comparatorToUse);
	}

	/**
	 * Invoke the given BeanDefinitionRegistryPostProcessor beans.
	 */
	private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {

		for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanDefinitionRegistry(registry);
		}
	}

	/**
	 * Invoke the given BeanFactoryPostProcessor beans.
	 */
	private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {

		for (BeanFactoryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}

	/**
	 * Register the given BeanPostProcessor beans.
	 */
	private static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {

		for (BeanPostProcessor postProcessor : postProcessors) {
			beanFactory.addBeanPostProcessor(postProcessor);
		}
	}


	/**
	 * BeanPostProcessor that logs an info message when a bean is created during
	 * BeanPostProcessor instantiation, i.e. when a bean is not eligible for
	 * getting processed by all BeanPostProcessors.
	 */
	private static final class BeanPostProcessorChecker implements BeanPostProcessor {

		private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);

		private final ConfigurableListableBeanFactory beanFactory;

		private final int beanPostProcessorTargetCount;

		public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
			this.beanFactory = beanFactory;
			this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			return bean;
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) {
			if (!(bean instanceof BeanPostProcessor) && !isInfrastructureBean(beanName) &&
					this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount) {
				if (logger.isInfoEnabled()) {
					logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() +
							"] is not eligible for getting processed by all BeanPostProcessors " +
							"(for example: not eligible for auto-proxying)");
				}
			}
			return bean;
		}

		private boolean isInfrastructureBean(@Nullable String beanName) {
			if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
				BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
				return (bd.getRole() == RootBeanDefinition.ROLE_INFRASTRUCTURE);
			}
			return false;
		}
	}

}
