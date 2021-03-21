package com.atguigu.test.circularReference.withEventuallyWrappedBean.withAopProxy;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private CircularDependencyA a;

    public void hello() {
        System.out.println("hello方法被调用  (在父类中)");
    }

    public CircularDependencyB getB() {
        return b;
    }

    public CircularDependencyA getA() {
        return a;
    }
}