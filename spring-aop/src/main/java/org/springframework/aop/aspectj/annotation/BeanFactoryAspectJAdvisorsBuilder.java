/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.aop.aspectj.annotation;

import org.aspectj.lang.reflect.PerClauseKind;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper for retrieving @AspectJ beans from a BeanFactory and building
 * Spring Advisors based on them, for use with auto-proxying.
 *
 * @author Juergen Hoeller
 * @see AnnotationAwareAspectJAutoProxyCreator
 * @since 2.0.2
 */
public class BeanFactoryAspectJAdvisorsBuilder {

	private final ListableBeanFactory beanFactory;

	private final AspectJAdvisorFactory advisorFactory;

    /**
     * <pre>
     * spring容器中所有的切面类的beanName
     *
     * 带有 @Aspect 注解的beanName:
     * {@code
     *     @Aspect
     *     public class LogAspects {
     *        @Pointcut("execution(public int com.atguigu.aop.MathCalculator.*(..))")
     *        public void pointCut(){};
     *     }
     * }
     * </pre>
     */
	@Nullable
	private volatile List<String> aspectBeanNames;

    /**
     *
     * 缓存了所有单例切面bean的增强器：
     * key:标注了@Aspect注解的切面类的beanName -> value:此切面类中所有的增强方法（即：标注了 @Around, @Before, @After, @AfterReturning, @AfterThrowing 注解的方法）
     */
	private final Map<String, List<Advisor>> advisorsCache = new ConcurrentHashMap<>();

    /**
     * 缓存非单例切面bean的切面实例创建工厂
     * key:标注了@Aspect注解的切面类的beanName -> value:此切面类的 MetadataAwareAspectInstanceFactory
     */
	private final Map<String, MetadataAwareAspectInstanceFactory> aspectFactoryCache = new ConcurrentHashMap<>();


	/**
	 * Create a new BeanFactoryAspectJAdvisorsBuilder for the given BeanFactory.
	 *
	 * @param beanFactory the ListableBeanFactory to scan
	 */
	public BeanFactoryAspectJAdvisorsBuilder(ListableBeanFactory beanFactory) {
		this(beanFactory, new ReflectiveAspectJAdvisorFactory(beanFactory));
	}

	/**
	 * Create a new BeanFactoryAspectJAdvisorsBuilder for the given BeanFactory.
	 *
	 * @param beanFactory    the ListableBeanFactory to scan
	 * @param advisorFactory the AspectJAdvisorFactory to build each Advisor with
	 */
	public BeanFactoryAspectJAdvisorsBuilder(ListableBeanFactory beanFactory, AspectJAdvisorFactory advisorFactory) {
		Assert.notNull(beanFactory, "ListableBeanFactory must not be null");
		Assert.notNull(advisorFactory, "AspectJAdvisorFactory must not be null");
		this.beanFactory = beanFactory;
		this.advisorFactory = advisorFactory;
	}


    /**
     * <pre>
     *
     * 构建容器中所有切面类的advice（存在@Aspect注解的类）
     * 对于第一次调用此方法：
     * 1、从 beanFactory 这个spring容器中获取所有的bean，判断是否为切面类（存在@Aspect注解的类）
     * 2、如果是：则作为切面类（存在@Aspect注解的类）来解析当前这个bean。
     * 3、解析过程中，如果是切面bean是单例的：
     *        把切面类（存在@Aspect注解的类）中对应的增强器缓存到 advisorsCache 中。（增强器是由标注了 @Around, @Before, @After, @AfterReturning, @AfterThrowing 注解的方法包装成的）
     *    如果切面bean不是单例的：
     *        把切面的增强器创建工程，缓存到 aspectFactoryCache 中。
     *
     * 对于非第一次调用此方法：
     *  从缓存中直接获取所有的增强器。
     * </pre>
     *
     * Look for AspectJ-annotated aspect beans in the current bean factory,
	 * and return to a list of Spring AOP Advisors representing them.
	 * <p>Creates a Spring Advisor for each AspectJ advice method.
	 *
	 * @return the list of {@link org.springframework.aop.Advisor} beans
	 * @see #isEligibleBean
	 */
	public List<Advisor> buildAspectJAdvisors() {
	    // 已经处理过的所有切面类（存在@Aspect注解的类）的beanName.
		List<String> aspectNames = this.aspectBeanNames;

		if (aspectNames == null) {
			synchronized (this) {
				aspectNames = this.aspectBeanNames;
				if (aspectNames == null) {
					List<Advisor> advisors = new ArrayList<>();
					aspectNames = new ArrayList<>();
					// 获取所有 beanName。判断这些bean是否是切面类（存在@Aspect注解的类）
					String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this.beanFactory, Object.class, true, false);
					// 循环所有 beanName 找出对应的增强方法
					for (String beanName : beanNames) {
						// 不合格的切面(aspect) bean  就 continue，子类覆盖此方法。本类中默认返回true,
						// 这里调用的是 BeanFactoryAspectJAdvisorsBuilderAdapter.java 的方法，用正则匹配合格的bean
						if (!isEligibleBean(beanName)) {
							continue;
						}
						// We must be careful not to instantiate beans eagerly as in this case they
						// would be cached by the Spring container but would not have been weaved.
						// 我们必须小心不要急切地实例化bean，因为在这种情况下，它们将被Spring容器缓存，但不会被编织。

						// 获取 bean 的类型
						Class<?> beanClass = this.beanFactory.getType(beanName);
						if (beanClass == null) {
							continue;
						}
						// 判断当前bean是否是切面(Aspect)，如果是切面，就使用
						if (this.advisorFactory.isAspect(beanClass)) {

							aspectNames.add(beanName);
							AspectMetadata amd = new AspectMetadata(beanClass, beanName);

							// 当切面类注释的@Aspect注解value属性为空 并且 父类为Object时，getKind() 就是 PerClauseKind.SINGLETON。
							// 也就是多数情况都是走if成立的逻辑。
							if (amd.getAjType().getPerClause().getKind() == PerClauseKind.SINGLETON) {
                                MetadataAwareAspectInstanceFactory factory = new BeanFactoryAspectInstanceFactory(this.beanFactory, beanName);

                                // 解析切面类（存在@Aspect注解的类）中的增强器。
								// 比如标注了 @Around, @Before, @After, @AfterReturning, @AfterThrowing 注解的方法。
								List<Advisor> classAdvisors = this.advisorFactory.getAdvisors(factory);

								if (this.beanFactory.isSingleton(beanName)) {
									// 注释1：如果当前切面bean是单例的，
									// 则把解析出来的 增强器 缓存起来。以后从缓存中根据切面beanName获取就行了。
									this.advisorsCache.put(beanName, classAdvisors);
								} else {
									// 注释2：如果当前切面bean 不是单例的，
									// 则在 aspectFactoryCache 缓存中记录一个 ‘切面实例化工程factory’ BeanFactoryAspectInstanceFactory
									this.aspectFactoryCache.put(beanName, factory);
								}
								advisors.addAll(classAdvisors);
							} else {
								// Per target or per this.
								if (this.beanFactory.isSingleton(beanName)) {
									throw new IllegalArgumentException("Bean with name '" + beanName + "' is a singleton, but aspect instantiation model is not singleton");
								}
								MetadataAwareAspectInstanceFactory factory = new PrototypeAspectInstanceFactory(this.beanFactory, beanName);
								this.aspectFactoryCache.put(beanName, factory);
								advisors.addAll(this.advisorFactory.getAdvisors(factory));
							}
						}
					}
					this.aspectBeanNames = aspectNames;
					return advisors;
				}
			}
		}

		// 如果当前容器中没有任何切面bean，就说明没有任何增强器。则返回空集合。
		if (aspectNames.isEmpty()) {
			return Collections.emptyList();
		}

		// 从缓存中获取所有切面类（存在@Aspect注解的类）的增强器。
		List<Advisor> advisors = new ArrayList<>();
		for (String aspectName : aspectNames) {
			List<Advisor> cachedAdvisors = this.advisorsCache.get(aspectName);
			if (cachedAdvisors != null) {
				advisors.addAll(cachedAdvisors);
			} else {
			    // 如果 this.advisorsCache.get(aspectName) 获取为空，说明当前切面bean不是单例的。
                // 则使用第一次调用 buildAspectJAdvisors() 方法的时候（见此方法中的 注释2 ），存放的 factory 重新获取一下 aspectName这个切面bean的增强器。
				MetadataAwareAspectInstanceFactory factory = this.aspectFactoryCache.get(aspectName);
				advisors.addAll(this.advisorFactory.getAdvisors(factory));
			}
		}
		return advisors;
	}

	/**
	 * Return whether the aspect bean with the given name is eligible.
	 *
	 * @param beanName the name of the aspect bean
	 * @return whether the bean is eligible
	 */
	protected boolean isEligibleBean(String beanName) {
		return true;
	}

}
