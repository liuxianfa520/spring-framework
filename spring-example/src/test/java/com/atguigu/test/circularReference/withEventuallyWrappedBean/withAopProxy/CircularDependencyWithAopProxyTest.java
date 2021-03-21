package com.atguigu.test.circularReference.withEventuallyWrappedBean.withAopProxy;


import com.atguigu.test.circularReference.withEventuallyWrappedBean.withEventuallyWrappedBean.CircularDependencyWithEventuallyWrappedBeanTest;

import org.junit.Test;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.util.Assert;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 8/16 19:33
 */
public class CircularDependencyWithAopProxyTest {

    /**
     * <pre>
     * 此测试用例的目的:
     * 之前觉得aop生成代理对象,会产生 {@link CircularDependencyWithEventuallyWrappedBeanTest#test()} 这种循环依赖.
     * 所以就想写测试用例证明一下.
     * 结果发现:[aop生成代理对象并不会产生上述循环依赖.]
     *
     *
     *
     * 运行之后,这里并不会抛出 {@link BeanCurrentlyInCreationException} 异常.
     *
     * 原因:
     *  aop生成代理对象,有两种情况:
     *  1、如果存在循环依赖,则会使用 {@link AbstractAutoProxyCreator#getEarlyBeanReference} 来给早期暴露的对象,创建代理对象.
     *  2、如果不存在循环依赖,则会使用 {@link AbstractAutoProxyCreator#postProcessAfterInitialization},在bean初始化完毕之后,来创建代理对象.
     *  - 其实也可以看 {@link AbstractAutoProxyCreator#wrapIfNecessary} 方法,此方法被上面两个方法调用.所以aop使用spring的两个扩展点来创建代理对象.
     *  - {@link AbstractAutoProxyCreator#wrapIfNecessary} 方法中,当一个bean创建过代理对象之后,会把代理对象缓存起来.
     *  - 生产缓存的key使用的是方法: {@link AbstractAutoProxyCreator#getCacheKey}
     *
     * </pre>
     */
    @Test
    public void test() {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(CircularReferenceConfig.class);
        applicationContext.refresh();

        CircularDependencyA a = applicationContext.getBean(CircularDependencyA.class);
        CircularDependencyB b = applicationContext.getBean(CircularDependencyB.class);
        a.hello();

        System.out.println();
        System.out.println(String.format("a对象类名:[%s]\n是否为aop增强之后的对象?%s", a.getClass().getSimpleName(), a instanceof org.springframework.cglib.proxy.Factory));

        Assert.notNull(a);
        Assert.notNull(a.getA());
        Assert.notNull(b.getA());

        Assert.notNull(b);
        Assert.notNull(a.getB());
    }
}