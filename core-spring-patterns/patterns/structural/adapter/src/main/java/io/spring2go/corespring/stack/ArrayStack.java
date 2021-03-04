package io.spring2go.corespring.stack;

// ���������ջʵ��
public class ArrayStack<T> implements IStack<T> {

	private static final int DEFAULT_CAPACITY = 15;
	private int top; // ָ��ջ��Ԫ��
	private T[] array;

	// region ���캯��
	/**
	 * ����һ��ջ��ָ����ʼ����
	 */
	public ArrayStack(int initialCapacity) {
		if (initialCapacity <= 0)
			array = (T[]) new Object[DEFAULT_CAPACITY];
		else
			array = (T[]) new Object[initialCapacity];

		top = -1; // ջ��
	}

	/**
	 * ����һ��ջ��ʹ�ó�ʼ����
	 */
	public ArrayStack() {
		this(DEFAULT_CAPACITY);
	}
	// endregion

	/**
	 * ���ջ�Ƿ�Ϊ��
	 */
	public boolean isEmpty() {
		return top == -1;
	}

	/**
	 * �Ƴ�������ջ��Ԫ��
	 */
	public T pop() throws StackException {
		T x = peek();
		array[top] = null; // ȷ�����󱻻���
		top--;
		return x;
	}

	/**
	 * ����ջ��Ԫ�أ����ǲ��Ƴ�
	 */
	public T peek() throws StackException {
		if (isEmpty())
			throw new StackException("Stack is empty");
		return array[top];
	}

	/**
	 * ��ջ�����һ����Ԫ��
	 */
	public void push(T e) throws StackException {
		if (top == array.length)
			throw new StackException("Stack has overflowed");
		top++;
		array[top] = e;
	}

	/**
	 * ���ջԪ��
	 */
	public void clear() {
		for (int i = 0; i <= top; i++) {
			array[i] = null;
		}
		top = -1;
	}

	/**
	 * ����ջ���ַ�����ʾ
	 */
	public String toString() {
		if (isEmpty())
			return "[ ]";

		StringBuffer out = new StringBuffer("[");
		for (int i = 0; i < top; i++)
			out.append(array[i] + ", ");

		out.append(array[top] + "]");
		return out.toString();
	}

}
