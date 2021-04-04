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

package org.springframework.aop.support;

import java.lang.reflect.Method;

import org.springframework.aop.MethodMatcher;
import org.springframework.lang.Nullable;

/**
 * Convenient abstract superclass for static method matchers, which don't care
 * about arguments at runtime.
 *
 * 静态方法匹配器
 * 注意:这里的'静态方法'不是指有static修饰的方法.
 *      而是说何时判断advice能否增强指定方法:
 *      - 是在创建bean过程中进行matches匹配判断?         isRuntime()返回false
 *      - 还是在目标方法被调用时,进行matches匹配判断?     isRuntime()返回true
 *
 *      这里的 {@link StaticMethodMatcher#isRuntime} 方法都是返回false.说明都是在bean创建时判断.
 *
 * @author Rod Johnson
 */
public abstract class StaticMethodMatcher implements MethodMatcher {

	@Override
	public final boolean isRuntime() {
		return false;
	}

	@Override
	public final boolean matches(Method method, @Nullable Class<?> targetClass, Object... args) {
		// should never be invoked because isRuntime() returns false
        // 此方法永远不会被调用,因为 isRuntime() 返回 false
		throw new UnsupportedOperationException("Illegal MethodMatcher usage");
	}

}
