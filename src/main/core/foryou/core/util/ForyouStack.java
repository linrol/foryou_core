package foryou.core.util;

import java.util.LinkedList;

/**
 * @author 罗林 E-mail:1071893649@qq.com
 * @version 创建时间：2018年5月7日 下午3:42:52 类说明
 */
public class ForyouStack<T> {

	private LinkedList<T> linkedlist = new LinkedList<T>();

	/**
	 * 入栈 将元素加入LinkedList容器 (即插入到链表的第一个位置)
	 */
	public synchronized void push(T s) {
		linkedlist.addFirst(s);
	}

	/**
	 * 取出堆栈中最上面的元素 (即取出链表linkedList的第一个元素)
	 * 
	 * @return
	 */
	public T peek() {
		return linkedlist.getFirst();
	}

	/**
	 * 出栈 取出并删除最上面的元素 (即移出linkedList的第一个元素)
	 * 
	 * @return
	 */
	public T pop() {
		return linkedlist.removeFirst();
	}

	/**
	 * 判断堆栈是否为空 (即判断 linkedList是否为空)
	 * 
	 * @return
	 */
	public boolean empty() {
		return linkedlist.isEmpty();
	}

	/**
	 * 获取元素个数
	 * 
	 * @return
	 */
	public int size() {
		return linkedlist.size();
	}
}
