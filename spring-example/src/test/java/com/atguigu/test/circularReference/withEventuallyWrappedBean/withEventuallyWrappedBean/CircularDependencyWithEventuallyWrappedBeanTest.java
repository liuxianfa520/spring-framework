package com.atguigu.test.circularReference.withEventuallyWrappedBean.withEventuallyWrappedBean;


import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 8/16 19:33
 */
public class CircularDependencyWithEventuallyWrappedBeanTest {

    /**
     * <pre>
     * 循环依赖:属于下面情况:
     * A类和B类形成循环依赖,
     * 但是由于在创建bean A过程中,A被包装了.
     * 这种情况可能会导致B中注入的A不是被包装之后的A.(画外音)
     *
     *
     * 运行之后,会报错:
     * {@code
     * org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'circularDependencyA': Bean with name 'circularDependencyA' has been injected into other beans [circularDependencyB] in its raw version as part of a circular reference, but has eventually been wrapped. This means that said other beans do not use the final version of the bean. This is often the result of over-eager type matching - consider using 'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.
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
     * 	at com.atguigu.test.circularReference.withEventuallyWrappedBean.withEventuallyWrappedBean.CircularDependencyWithEventuallyWrappedBeanTest.test(CircularDependencyWithEventuallyWrappedBeanTest.java:59)
     * }
     *
     * 解决:
     * 方案1、可以设置{@link AbstractAutowireCapableBeanFactory#allowRawInjectionDespiteWrapping}等于true (会存在问题)
     * 方案2、这中循环依赖[通常是由于过度渴望类型匹配而导致的 - 例如，考虑在关闭“ allowEagerInit”标志的情况下使用“ getBeanNamesOfType”。]
     *        note:中括号中的这句话是从spring源码中抄出来的,反正我还没看懂啥意思.
     * <p>
     * 如果想要看异常信息,则把 expected = UnsatisfiedDependencyException.class 去掉.
     *
     *
     * 画外音1:也就是 if (exposedObject == rawBeanInstance) = false 这种情况.
     * </pre>
     */
    @Test//(expected = UnsatisfiedDependencyException.class)
    public void test() {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(CircularReferenceConfig.class);
        applicationContext.refresh();
    }

    /**
     * 测试:解决方案1
     */
    @Test//(expected = UnsatisfiedDependencyException.class)
    public void testSolution_1() {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();

        // 对应 解决方案1 的代码.         (这里也是 BeanFactoryPostProcessor 使用案例的一种)
        applicationContext.addBeanFactoryPostProcessor(new BeanDefinitionRegistryPostProcessor() {
            @Override
            public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            }

            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                if (beanFactory instanceof AbstractAutowireCapableBeanFactory) {
                    // 设置{@link AbstractAutowireCapableBeanFactory#allowRawInjectionDespiteWrapping}等于true
                    // 存在的问题,见此方法最后一行代码.
                    ((AbstractAutowireCapableBeanFactory) beanFactory).setAllowRawInjectionDespiteWrapping(true);
                }
            }
        });

        applicationContext.register(CircularReferenceConfig.class);
        applicationContext.refresh();

        CircularDependencyA a = applicationContext.getBean(CircularDependencyA.class);
        CircularDependencyB b = applicationContext.getBean(CircularDependencyB.class);
        a.hello();

        System.out.println();
        System.out.println();
        System.out.println("从容器中获取的a对象和b中依赖的a对象是否为同一个对象?" + (a == b.a));
    }

    /**
     * 测试:解决方案2
     */
    @Test//(expected = UnsatisfiedDependencyException.class)
    public void testSolution_2() {
        // todo:还没看懂方案2
    }

}