package com.atguigu.test.circularReference.withEventuallyWrappedBean;


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
     * 但是由于在创建bean A过程中,A被包装了(或者被aop增强了(画外音1)),导致B中注入的A不是被包装(或aop增强之后的bean A)之后的A.(画外音2)
     *
     * 此时会抛出 {@link BeanCurrentlyInCreationException}
     *
     * 解决:
     * 方案1、可以设置{@link AbstractAutowireCapableBeanFactory#allowRawInjectionDespiteWrapping}等于true (会存在问题)
     * 方案2、这中循环依赖[通常是由于过度渴望类型匹配而导致的 - 例如，考虑在关闭“ allowEagerInit”标志的情况下使用“ getBeanNamesOfType”。]
     *        note:中括号中的这句话是从spring源码中抄出来的,反正我还没看懂啥意思.
     * <p>
     * 如果想要看异常信息,则把 expected = UnsatisfiedDependencyException.class 去掉.
     *
     *
     * 画外音1:一个bean被aop增强之后,会生成原对象的子类对象.
     * 画外音2:也就是 if (exposedObject == rawBeanInstance) = false 这种情况.
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