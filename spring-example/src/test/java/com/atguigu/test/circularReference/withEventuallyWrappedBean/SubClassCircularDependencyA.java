package com.atguigu.test.circularReference.withEventuallyWrappedBean;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 3/20 23:45
 */
public class SubClassCircularDependencyA extends CircularDependencyA {

    private CircularDependencyA circularDependencyA;
    public SubClassCircularDependencyA(CircularDependencyA circularDependencyA) {
        this.circularDependencyA = circularDependencyA;
    }

    @Override
    public void hello() {
        System.out.println("在子类中:目标方法执行之前.");
        circularDependencyA.hello();
        System.out.println("在子类中:目标方法执行之后.");
    }
}
