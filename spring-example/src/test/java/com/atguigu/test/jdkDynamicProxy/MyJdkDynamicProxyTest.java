package com.atguigu.test.jdkDynamicProxy;

import org.springframework.aop.framework.AopProxy;

import sun.misc.ProxyGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <pre>
 * ����&ѧϰjdk��̬����
 * 1��ʹ��Proxy.newProxyInstance()�������ɴ������,������������:
 *    1)�������
 *    2)Ŀ�����Ľӿ�����
 *      ��ע:ֱ��ʹ�� target.getClass().getInterfaces() ����
 *    3)ʵ��InvocationHandler��invoke����.
 * 2������proxy���������κη������ᱻ��ǿ;ֱ�ӵ���targetĿ�������κη���,�����ᱻ��ǿ.
 * 3�������Ĵ������class:
 *      ��ע:�����ɵĴ������class�ļ��п��Կ���,��������ڲ����з�������ȥ���� {@link InvocationHandler#invoke} ����.
 * {@code
 * import com.atguigu.test.jdkProxy.LoginService;
 * import java.lang.reflect.InvocationHandler;
 * import java.lang.reflect.Method;
 * import java.lang.reflect.Proxy;
 * import java.lang.reflect.UndeclaredThrowableException;
 *
 * public final class LoginServiceImpl$Proxy extends Proxy implements LoginService {
 *     private static Method m1;
 *     private static Method m2;
 *     private static Method m3;
 *     private static Method m0;
 *
 *     public LoginServiceImpl$Proxy(InvocationHandler var1) throws  {
 *         super(var1);
 *     }
 *
 *     public final boolean equals(Object var1) throws  {
 *         try {
 *             return (Boolean)super.h.invoke(this, m1, new Object[]{var1});
 *         } catch (RuntimeException | Error var3) {
 *             throw var3;
 *         } catch (Throwable var4) {
 *             throw new UndeclaredThrowableException(var4);
 *         }
 *     }
 *
 *     public final String toString() throws  {
 *         try {
 *             return (String)super.h.invoke(this, m2, (Object[])null);
 *         } catch (RuntimeException | Error var2) {
 *             throw var2;
 *         } catch (Throwable var3) {
 *             throw new UndeclaredThrowableException(var3);
 *         }
 *     }
 *
 *     public final void login(String var1, String var2) throws  {
 *         try {
 *             super.h.invoke(this, m3, new Object[]{var1, var2});
 *         } catch (RuntimeException | Error var4) {
 *             throw var4;
 *         } catch (Throwable var5) {
 *             throw new UndeclaredThrowableException(var5);
 *         }
 *     }
 *
 *     public final int hashCode() throws  {
 *         try {
 *             return (Integer)super.h.invoke(this, m0, (Object[])null);
 *         } catch (RuntimeException | Error var2) {
 *             throw var2;
 *         } catch (Throwable var3) {
 *             throw new UndeclaredThrowableException(var3);
 *         }
 *     }
 *
 *     static {
 *         try {
 *             m1 = Class.forName("java.lang.Object").getMethod("equals", Class.forName("java.lang.Object"));
 *             m2 = Class.forName("java.lang.Object").getMethod("toString");
 *             m3 = Class.forName("com.atguigu.test.jdkProxy.LoginService").getMethod("login", Class.forName("java.lang.String"), Class.forName("java.lang.String"));
 *             m0 = Class.forName("java.lang.Object").getMethod("hashCode");
 *         } catch (NoSuchMethodException var2) {
 *             throw new NoSuchMethodError(var2.getMessage());
 *         } catch (ClassNotFoundException var3) {
 *             throw new NoClassDefFoundError(var3.getMessage());
 *         }
 *     }
 * }
 * }
 * 4��Ϊʲôjdk��̬����,����ʵ�ֽӿ�?   ����P7
 *    ��:JAVA�﷨Ҫ��[���ǵ��̳С���ʵ��].
 *       ���������������:����Ķ�������Ѿ��̳���java.lang.reflect.Proxy��,����ֻ��Ҫ��[��������target]ʵ�ֽӿ�.
 * 5��jdk��̬������ŵ�:
 *     ����jdk�����api.������������jar��.
 * 6��jdk��̬�����ȱ��:
 *    ����Ҫ����Ҫ�������ʵ���Խӿ�.�Դ�����������.
 *    ���ܷ���,����jdk8�Ż���,�Ѿ���cglib�����.
 * 7��spring�ٷ��õ�jdk��̬����ʵ�ֵ�aopԴ��: org.springframework.aop.framework.JdkDynamicAopProxy
 * </pre>
 *
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2/9 14:08
 */
public class MyJdkDynamicProxyTest implements AopProxy, InvocationHandler {


    public static void main(String[] args) throws IOException {
        // ��jdk��̬�������ɵĴ�����class�ļ�,д�� [��ǰ��Ŀ��Ŀ¼+����] Ŀ¼��.
        // ���:sun.misc.ProxyGenerator#saveGeneratedFiles
        System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");


        // �������ԭʼ����,Ҳ���Գ�ΪĿ�����
        MyJdkDynamicProxyTest myJdkDynamicProxyTest = new MyJdkDynamicProxyTest(new LoginServiceImpl());
        LoginService proxy = (LoginService) myJdkDynamicProxyTest.getProxy();

        proxy.login("lxf", "asdf");
        System.out.println(proxy.toString());
        System.out.println(myJdkDynamicProxyTest.getTargetObject().toString());


        System.out.println();
        System.out.println("ֱ��ʹ�ñ�Ŀ��������login����:(û����ǿ��ִ��.)");
        ((LoginService) myJdkDynamicProxyTest.getTargetObject()).login("lxf", "asdf");

        // �����������ļ�
        generateClassFile((LoginServiceImpl) myJdkDynamicProxyTest.getTargetObject());
    }


    private Object targetObject;

    public MyJdkDynamicProxyTest(Object targetObject) {
        this.targetObject = targetObject;
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(this.targetObject.getClass().getClassLoader(), targetObject.getClass().getInterfaces(), this);
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, targetObject.getClass().getInterfaces(), this);
    }

    /**
     * ���ɴ�������class�ļ�.
     * <p>
     * ʹ�� {@code System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true"); } Ҳ���԰����ɵ�class�ļ��������. �����class�ļ��� [��ǰ��Ŀ��Ŀ¼+����] Ŀ¼��.
     *
     * @param target
     * @throws IOException
     */
    private static void generateClassFile(LoginServiceImpl target) throws IOException {
        String className = target.getClass().getSimpleName() + "$Proxy";
        String filePath = String.format(System.getProperty("java.io.tmpdir") + File.separator + "%s.class", className);
        byte[] $Proxies = ProxyGenerator.generateProxyClass(className, target.getClass().getInterfaces());
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        fileOutputStream.write($Proxies);
        fileOutputStream.flush();
        fileOutputStream.close();
        System.out.println("���ɴ������proxy����:" + filePath);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object invoke;
        try {
            System.out.println("@Before"); // ǰ����ǿ
            invoke = method.invoke(getTargetObject(), args); // ע������invoke������һ������,ʹ�õ���Ŀ�����target,������proxy����.���ʹ��proxy�ᱨ��
            System.out.println("@AfterReturning"); // ��Ŀ�귽������֮����ǿ
        } catch (Exception e) {
            System.err.println("@AfterThrowing         Exception:" + e.getMessage()); // ��Ŀ�귽���쳣ʱ����ǿ
            throw e;// ���Ŀ�귽�������쳣,��Ҫ���쳣�׳�ȥ.
        } finally {
            System.out.println("@After"); // ������ǿ
        }
        return invoke;
    }

    // ====== getter/setter ======
    public Object getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(Object targetObject) {
        this.targetObject = targetObject;
    }
}

interface LoginService {
    void login(String userName, String pwd);
}


class LoginServiceImpl implements LoginService {

    @Override
    public void login(String userName, String pwd) {
        System.out.println("��ǰ��¼�û���Ϊ:" + userName);
    }
}
