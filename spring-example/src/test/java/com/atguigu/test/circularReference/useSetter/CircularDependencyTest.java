package com.atguigu.test.circularReference.useSetter;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 8/16 19:33
 */
public class CircularDependencyTest {


    /**
     * 使用setter注入的单例bean 构成的循环依赖,spring可以解决.
     */
    @Test
    public void test() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(CircularReferenceConfig.class);


        CircularDependencyA a = applicationContext.getBean(CircularDependencyA.class);
        CircularDependencyB b = applicationContext.getBean(CircularDependencyB.class);


        System.out.println(a);
        System.out.println(b.circA);

        System.out.println(b);
        System.out.println(a.circB);

        Assert.assertNotNull(a.circB);
        Assert.assertNotNull(b.circA);


        applicationContext.close();
    }

}