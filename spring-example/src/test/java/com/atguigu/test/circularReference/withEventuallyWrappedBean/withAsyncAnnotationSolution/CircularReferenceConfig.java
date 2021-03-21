package com.atguigu.test.circularReference.withEventuallyWrappedBean.withAsyncAnnotationSolution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 8/16 19:32
 */
@Configuration
@ComponentScan("com.atguigu.test.circularReference.withEventuallyWrappedBean.withAsyncAnnotationSolution")
public class CircularReferenceConfig {

    @Bean
    public MyAsyncAnnotationBeanPostProcessor myAsyncAnnotationBeanPostProcessor() {
        MyAsyncAnnotationBeanPostProcessor bpp = new MyAsyncAnnotationBeanPostProcessor();
        bpp.setOrder(Ordered.LOWEST_PRECEDENCE);
        bpp.setProxyTargetClass(false);
        return bpp;
    }

}