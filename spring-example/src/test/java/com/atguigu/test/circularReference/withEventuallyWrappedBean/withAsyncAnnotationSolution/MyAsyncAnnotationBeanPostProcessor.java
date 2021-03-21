package com.atguigu.test.circularReference.withEventuallyWrappedBean.withAsyncAnnotationSolution;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 3/21 18:46
 */
public class MyAsyncAnnotationBeanPostProcessor extends AsyncAnnotationBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor {
    private static final Map<Object, Object> cache = new ConcurrentReferenceHashMap<>();

    private static final Object o = new Object();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        synchronized (o) {
            return getProxyObject(bean, beanName);
        }
    }

    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        synchronized (o) {
            return getProxyObject(bean, beanName);
        }
    }

    /**
     * 获取代理对象——先去缓存中获取,缓存中没有的话,就去创建代理对象.
     *
     * @param bean
     * @param beanName
     * @return
     */
    private Object getProxyObject(Object bean, String beanName) {
        Object cacheKey = getCacheKey(bean.getClass(), beanName);
        Object cacheProxyObject = cache.get(cacheKey);
        if (cacheProxyObject != null) {
            return cacheProxyObject;
        }

        Object proxyObject = super.postProcessAfterInitialization(bean, beanName);
        cache.put(cacheKey, proxyObject);
        return proxyObject;
    }

    protected Object getCacheKey(Class<?> beanClass, @Nullable String beanName) {
        if (StringUtils.hasLength(beanName)) {
            return (FactoryBean.class.isAssignableFrom(beanClass) ?
                    BeanFactory.FACTORY_BEAN_PREFIX + beanName : beanName);
        } else {
            return beanClass;
        }
    }
}