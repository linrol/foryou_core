package foryou.core.util;

/*如何编写工具类： 
 * 方法一： 
 * 1.把工具方法做成非static方法， 
 * 2.把工具类做成单例的。 
 * 那么要调用工具方法，必须先得到工具类。再调用方法 
 * 方法二： 
 * 1.把工具方法做成static方法 
 * 2.把工具类所有构造器全部私有化，或者把工具加上abstract的。 
 * 这么做的目的就是为了全部是类去调用工具方法。 
 *  
 * */
//根据反射的对象工厂，专门用来生成对象。为了使其他方法类也可以使用该方法，特意将该方法抽取到该类中。  
//单例模式：Enum  
public enum BeanFactory {
	INSTANCE;
	@SuppressWarnings("unchecked")
	public <T> T getBean(String className, Class<T> checkType) {
		try {
			Class<T> clz = (Class<T>) Class.forName(className);
			Object obj = clz.newInstance();
			// 需要检查checkedType是否是obj的字节码对象。
			if (!checkType.isInstance(obj)) {
				throw new IllegalArgumentException("对象和类型不兼容");
			}
			return  (T) obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}