package com.atguigu.test.circularReference.withEventuallyWrappedBean.withAsyncAnnotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 8/16 19:31
 */
@Component
public class CircularDependencyB {
    @Autowired
    private CircularDependencyA a;
}