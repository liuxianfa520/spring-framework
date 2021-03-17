package com.atguigu.test.circularReference.useConstructor;

import com.atguigu.circularReference.useConstructor.CircularReferenceConfig;

import org.junit.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 8/16 19:33
 */
public class CircularDependencyTest {


    /**
     * 构造方法 循环依赖
     *
     * 如果想要看异常信息,则把 expected = UnsatisfiedDependencyException.class 去掉.
     */
    @Test(expected = UnsatisfiedDependencyException.class)
    public void test() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(CircularReferenceConfig.class);
        applicationContext.close();
    }

}