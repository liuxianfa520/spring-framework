package com.atguigu.test.circularReference.withEventuallyWrappedBean.withAsyncAnnotation;


import org.junit.Test;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.ProxyAsyncConfiguration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 8/16 19:33
 */
public class CircularDependencyWithAsyncTest {

    /**
     * <pre>
     * 此测试用例的目的:
     * 验证 {@link org.springframework.scheduling.annotation.Async} 是否会产生第四种循环依赖异常?
     *
     * 运行之后,会报错:
     * {@code
     * org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'circularDependencyA': Bean with name 'circularDependencyA' has been injected into other beans [circularDependencyA] in its raw version as part of a circular reference, but has eventually been wrapped. This means that said other beans do not use the final version of the bean. This is often the result of over-eager type matching - consider using 'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.
     *
     * 	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:704)
     * 	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:534)
     * 	at org.springframework.beans.factory.support.AbstractBeanFactory$1.getObject(AbstractBeanFactory.java:398)
     * 	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:303)
     * 	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:394)
     * 	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:243)
     * 	at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:796)
     * 	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:1018)
     * 	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:613)
     * 	at com.atguigu.test.circularReference.withEventuallyWrappedBean.withAsyncAnnotation.CircularDependencyWithAopProxyTest.test(CircularDependencyWithAopProxyTest.java:32)
     * }
     *
     * 原因:
     * 1、启用异步需要使用 @{@link EnableAsync} 注解.
     *    - @Import(AsyncConfigurationSelector.class)
     *    - 默认会向spring容器中注入:{@link ProxyAsyncConfiguration}
     *    - 向容器中注入:{@link AsyncAnnotationBeanPostProcessor}
     *    - 本质上也是使用后置处理器,在bean初始化之后{@link AsyncAnnotationBeanPostProcessor#postProcessAfterInitialization},使用aop创建代理对象来实现的异步.
     * 2、但是我们看到 {@link AsyncAnnotationBeanPostProcessor} 这个后置处理器,只会在 [bean初始化完毕之后postProcessAfterInitialization]才会创建代理对象.
     * 3、和原生的aop的区别是:aop会解决循环依赖;而异步使用的这个后置处理器,不会去解决循环依赖.
     *
     *
     * 问题:
     * 如果我们使用子类实现一下{@link AsyncAnnotationBeanPostProcessor},
     * 让其也有 {@link SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference} 的能力,是否就能解决循环依赖呢?
     * 解决测试代码,见: {@link }
     * </pre>
     */
    @Test
    public void test() throws InterruptedException {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(CircularReferenceConfig.class);
        applicationContext.refresh();

        CircularDependencyA a = applicationContext.getBean(CircularDependencyA.class);
        System.out.println("测试用例方法所在线程id:" + Thread.currentThread().getName());
        a.hello();

        System.out.println();
        System.out.println(String.format("a对象类名:[%s]\n是否为aop增强之后的对象?%s", a.getClass().getSimpleName(), a instanceof org.springframework.cglib.proxy.Factory));


        Thread.sleep(5 * 1000);
    }

}