package com.atguigu.test.circularReference.withEventuallyWrappedBean.withAsyncAnnotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 8/16 19:31
 */
@Component
public class CircularDependencyA {

    @Autowired
    private CircularDependencyB b;

    @Async
    public void hello() {
        System.out.println("hello  线程id:" + Thread.currentThread().getName());
    }
}