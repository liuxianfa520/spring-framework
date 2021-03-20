package com.atguigu.test.circularReference.withEventuallyWrappedBean.withEventuallyWrappedBean;

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
    public CircularDependencyB b;

    public void hello() {
        System.out.println("hello方法被调用  (在父类中)");
    }

}