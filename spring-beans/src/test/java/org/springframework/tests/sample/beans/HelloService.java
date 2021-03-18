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

package org.springframework.tests.sample.beans;

public class HelloService {
    public HelloService() {
        System.out.println("HelloService 构造方法.");
    }

    public void hello() {
        System.out.println("hello");
    }

    public void init() {
        System.out.println("init方法执行了");
    }

    public void destory(){
        System.out.println("distory 方法执行了.");
    }

}
