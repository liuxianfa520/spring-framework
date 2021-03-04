package io.spring2go.corespring.singleton_v2_1;

// �̰߳�ȫ����
public class ThreadSafeSingleton {
	private static ThreadSafeSingleton INSTANCE;
	
	// ˽�й��캯�������ⱻ�ͻ��˴���ʹ��
	private ThreadSafeSingleton(){}

	public static synchronized ThreadSafeSingleton getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ThreadSafeSingleton();
		}
		return INSTANCE;
	}
}
