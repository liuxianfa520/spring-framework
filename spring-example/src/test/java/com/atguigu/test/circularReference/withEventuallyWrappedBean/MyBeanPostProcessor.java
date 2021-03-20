package com.atguigu.test.circularReference.withEventuallyWrappedBean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 3/20 23:36
 */
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof CircularDependencyA) {
            // 在bean初始化之前,修改bean对象.(这里仅仅是使用子类包装了一下,在实际开发中,一般都是使用aop生成的子类对象)
            return new SubClassCircularDependencyA((CircularDependencyA) bean);
        }
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }
}
