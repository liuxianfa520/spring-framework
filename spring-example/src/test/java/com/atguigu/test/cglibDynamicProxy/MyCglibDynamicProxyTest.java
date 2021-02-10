package com.atguigu.test.cglibDynamicProxy;

import org.springframework.aop.framework.AopProxy;
import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.io.File;
import java.lang.reflect.Method;

/**
 * <pre>
 * ����&ѧϰʹ��cglibʵ�ֶ�̬����
 * 1��ʹ��cglib��̬����ԭ��:
 *    ʹ��ASM�ײ��ֽ���������,��̬���ɵĴ������ΪĿ����������.
 *    �������е����з�����,������ {@link MethodInterceptor} ����.
 *    ���� �޷���final���final����������ǿ. ��ע:{@link Enhancer#generateClass}���ж���superClass�����final����,���׳��쳣.
 * 2���ŵ�:
 *      ����Ҫ�󱻴�����ʵ�ֽӿ�.�Դ��������Խϵ�.
 * 3��ȱ��:
 *      ��Ҫ���뵥����cglib����asm����.����ֻ��һ����.�����汾����ά���ȷ���,����û��jdkԭ����̬����������.
 *      �޷���final���final����������ǿ.(��Ϊ�����ɵ�����,final���final���������ܱ���д.)
 * 4������:
 *      ��˵��jdk8�Ķ�̬����Ա�,���ܷ����Ѿ������.
 * 5����Ƶ��ַ:https://www.bilibili.com/video/BV1SJ411v7fq
 * 6��
 * </pre>
 *
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2/10 14:58
 */
public class MyCglibDynamicProxyTest implements AopProxy, MethodInterceptor {
    public static void main(String[] args) {
        String projectRootPath = new File("").getAbsolutePath();
        System.out.println(String.format("��ȡ��ǰ��Ŀ��Ŀ¼:[%s]", projectRootPath));

        // ����������debugģʽ:�����ɵĴ�����class�ļ������ָ���ļ�����.
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, projectRootPath);


        MyCglibDynamicProxyTest myCglibDynamicProxyTest = new MyCglibDynamicProxyTest(new LoginService());

        Object proxy = myCglibDynamicProxyTest.getProxy();
        ((LoginService) proxy).login("lxf", "asdf");
    }

    private Object targetObject;

    public MyCglibDynamicProxyTest(Object targetObject) {
        this.targetObject = targetObject;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object result;
        try {
            System.out.println("@Before");
            result = methodProxy.invokeSuper(o, args);// ���ø��෽��.��Ϊcglibʵ��ԭ����Ƕ�̬����һ��Ŀ����������.
            System.out.println("@AfterReturning");
        } catch (Exception e) {
            System.out.println("@AfterThrwoing");
            throw e;
        } finally {
            System.out.println("@After");
        }
        return result;
    }

    @Override
    public Object getProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetObject.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetObject.getClass());
        enhancer.setCallback(this);
        enhancer.setClassLoader(classLoader);
        return enhancer.create();
    }

    // ====== getter/setter ======
    public Object getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(Object targetObject) {
        this.targetObject = targetObject;
    }
}


class LoginService {
    public void login(String userName, String pwd) {
        System.out.println("��ǰ��¼�û���:" + userName);
    }
}