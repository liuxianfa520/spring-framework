package com.atguigu.test.circularReference.withEventuallyWrappedBean.withAopProxy;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 3/21 2:25
 */
@Component
@Aspect
public class LogAspect {
    @Around("execution(* com.atguigu.test.circularReference.withEventuallyWrappedBean.withAopProxy.CircularDependencyA.hello(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        System.out.println("log aop: before run.");
        return point.proceed();
    }
}
