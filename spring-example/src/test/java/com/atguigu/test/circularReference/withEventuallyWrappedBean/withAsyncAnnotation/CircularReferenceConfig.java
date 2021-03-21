package com.atguigu.test.circularReference.withEventuallyWrappedBean.withAsyncAnnotation;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 8/16 19:32
 */
@Configuration
@ComponentScan("com.atguigu.test.circularReference.withEventuallyWrappedBean.withAsyncAnnotation")
@EnableAsync
public class CircularReferenceConfig {
}