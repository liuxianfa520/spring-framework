/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.aop.aspectj;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.aop.AfterAdvice;

/**
 * Spring AOP advice wrapping an AspectJ after-throwing advice method.
 *
 * @author Rod Johnson
 * @since 2.0
 */
@SuppressWarnings("serial")
public class AspectJAfterThrowingAdvice extends AbstractAspectJAdvice
		implements MethodInterceptor, AfterAdvice, Serializable {

	public AspectJAfterThrowingAdvice(
			Method aspectJBeforeAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {

		super(aspectJBeforeAdviceMethod, pointcut, aif);
	}


	@Override
	public boolean isBeforeAdvice() {
		return false;
	}

	@Override
	public boolean isAfterAdvice() {
		return true;
	}

	@Override
	public void setThrowingName(String name) {
		setThrowingNameNoCheck(name);
	}

	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
        // 当前 AspectJAfterThrowingAdvice 这个 方法拦截器(MethodInterceptor),用来处理 @AfterThrowing 注解.
        // 使用try-catch 包装目标方法的调用.当目标方法的调用出现异常时,就进入catch逻辑:去调用aop切面中声明的 @AfterThrowing 方法.
		try {
			return mi.proceed();
		}
		catch (Throwable ex) {
            // 目标方法执行抛出异常时，会进入此catch。判断aop是否存在 @AfterThrowing(value="pointCut()",throwing="exception")方法，如果存在则调用。
            if (shouldInvokeOnThrowing(ex)) {
                invokeAdviceMethod(getJoinPointMatch(), null, ex);
			}
			throw ex;
		}
	}

	/**
	 * In AspectJ semantics, after throwing advice that specifies a throwing clause
	 * is only invoked if the thrown exception is a subtype of the given throwing type.
	 */
	private boolean shouldInvokeOnThrowing(Throwable ex) {
		return getDiscoveredThrowingType().isAssignableFrom(ex.getClass());
	}

}
