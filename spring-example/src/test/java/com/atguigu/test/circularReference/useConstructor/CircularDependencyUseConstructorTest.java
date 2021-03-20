package com.atguigu.test.circularReference.useConstructor;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 8/16 19:33
 */
public class CircularDependencyUseConstructorTest {


    /**
     * 构造方法 循环依赖
     * <p>
     * 如果想要看异常信息,则把 expected = UnsatisfiedDependencyException.class 去掉.
     */
    @Test//(expected = UnsatisfiedDependencyException.class)
    public void test() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(CircularReferenceConfig.class);
        CircularDependencyA a = applicationContext.getBean(CircularDependencyA.class);
        CircularDependencyB b = applicationContext.getBean(CircularDependencyB.class);


        System.out.println(a);
        System.out.println(b.circA);

        System.out.println(b);
        System.out.println(a.circB);

        applicationContext.close();
    }

}