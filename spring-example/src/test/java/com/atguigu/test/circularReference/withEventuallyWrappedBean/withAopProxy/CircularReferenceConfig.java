package com.atguigu.test.circularReference.withEventuallyWrappedBean.withAopProxy;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 8/16 19:32
 */
@Configuration
@ComponentScan("com.atguigu.test.circularReference.withEventuallyWrappedBean.withAopProxy")
@EnableAspectJAutoProxy
public class CircularReferenceConfig {
}