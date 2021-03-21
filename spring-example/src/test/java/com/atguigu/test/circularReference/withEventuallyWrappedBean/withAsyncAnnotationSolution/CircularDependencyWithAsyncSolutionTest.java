package com.atguigu.test.circularReference.withEventuallyWrappedBean.withAsyncAnnotationSolution;


import com.atguigu.test.circularReference.withEventuallyWrappedBean.withAsyncAnnotation.CircularDependencyWithAsyncTest;

import org.junit.Test;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 8/16 19:33
 */
public class CircularDependencyWithAsyncSolutionTest {

    /**
     * <pre>
     * 需要解决的问题在:{@link CircularDependencyWithAsyncTest#test()}
     *
     *
     *
     * 如果我们使用子类实现一下{@link AsyncAnnotationBeanPostProcessor},
     * 让其也有 {@link SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference} 的能力,
     * 是否就能解决使用 {@link Async} 的循环依赖呢?
     *
     * fixme: CircularDependencyWithAsyncSolutionTest还是报错
     * </pre>
     */
    @Test
    public void testSolution() throws InterruptedException {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(CircularReferenceConfig.class);
        applicationContext.refresh();


        CircularDependencyA
                a = applicationContext.getBean(CircularDependencyA.class);
        System.out.println("测试用例方法所在线程id:" + Thread.currentThread().getName());
        a.hello();

        System.out.println();
        System.out.println(String.format("a对象类名:[%s]\n是否为aop增强之后的对象?%s", a.getClass().getSimpleName(), a instanceof org.springframework.cglib.proxy.Factory));

        Thread.sleep(5000);
    }
}