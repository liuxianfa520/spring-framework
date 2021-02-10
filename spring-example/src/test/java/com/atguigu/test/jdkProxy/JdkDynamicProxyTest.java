package com.atguigu.test.jdkProxy;

import sun.misc.ProxyGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <pre>
 * ԭ��jdk��̬���������
 * 1��ʹ��Proxy.newProxyInstance()�������ɴ������,������������:
 *    1)�������
 *    2)Ŀ�����Ľӿ�����
 *      ��ע:ֱ��ʹ�� target.getClass().getInterfaces() ����
 *    3)ʵ��InvocationHandler��invoke����.
 *      ��ע:�� {@link JdkDynamicProxyTest#generateClassFile} ���ɵĴ������class�ļ��п��Կ���,��������ڲ����з�������ȥ���� {@link InvocationHandler#invoke} ����.
 * 2������proxy���������κη��������ӡĿ�귽��ִ��ʱ��;ֱ�ӵ���targetĿ�������κη���,�������ӡ.
 * 3�������Ĵ������class:
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
 * </pre>
 *
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2/9 14:08
 */
public class JdkDynamicProxyTest {

    public static void main(String[] args) throws IOException {
        // ��jdk��̬�������ɵĴ�����class�ļ�,д�� [��ǰ��Ŀ��Ŀ¼+����] Ŀ¼��.
        // ���:sun.misc.ProxyGenerator#saveGeneratedFiles
        System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");

        // �������ԭʼ����,Ҳ���Գ�ΪĿ�����
        LoginServiceImpl target = new LoginServiceImpl();
        LoginService proxy = getProxy(target);

        proxy.login("lxf", "asdf");
        System.out.println(proxy.toString());
        System.out.println(target.toString());


        System.out.println();
        System.out.println("ֱ��ʹ�ñ�Ŀ��������login����:(û����ǿ��ִ��.)");
        target.login("lxf", "asdf");

        System.out.println();
        System.out.println();

        generateClassFile(target);
    }

    /**
     * ���ɴ������
     *
     * @param target
     * @return
     */
    private static LoginService getProxy(LoginServiceImpl target) {
        // jdk��̬�������ɵĴ������
        return (LoginService) Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy1, Method method, Object[] args) throws Throwable {
                System.out.println("@Before");
                long l = System.currentTimeMillis();
                Object invoke = null;
                try {
                    Proxy p = (Proxy) proxy1;


                    // ע������invoke������һ������,ʹ�õ���Ŀ�����target,������proxy����.���ʹ��proxy�ᱨ��
                    invoke = method.invoke(target, args);
                    System.out.println("@AfterReturning");
                } catch (Exception e) {
                    System.out.println("@AfterThrowing         Exception:" + e.getMessage());
                } finally {
                    System.out.println("@After");
                }
                System.out.println(String.format("[%s]����ִ�л���ʱ��:%s����", method.getName(), System.currentTimeMillis() - l));
                return invoke;
            }
        });
    }

    /**
     * ���ɴ�������class�ļ�.
     *
     * ʹ�� {@code System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true"); } Ҳ���԰����ɵ�class�ļ��������.
     * �����class�ļ��� [��ǰ��Ŀ��Ŀ¼+����] Ŀ¼��.
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
