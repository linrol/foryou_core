package foryou.core.entity;

import java.lang.reflect.Method;
import java.util.Map;

/** 
* @author 罗林 E-mail:1071893649@qq.com 
* @version 创建时间：2018年1月11日 上午11:43:01 
* 类说明 
*/
public class ControllerMethod {

	/**
	 * 方法原型
	 */
	private Method method;
	
	/**
	 * 方法上的参数类型原型
	 * String:参数名称 arg
	 * Class<?>:参数类型 class [Ljava.lang.String
	 */
	private Map<String,Class<?>> parameterTypeMap;
	
	/**
	 * 方法同步调用，多实例或者多线程同一时刻只能唯一请求
	 */
	private Boolean methodSynchronized = false;
	
	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Map<String, Class<?>> getParameterTypeMap() {
		return parameterTypeMap;
	}

	public void setParameterTypeMap(Map<String, Class<?>> parameterTypeMap) {
		this.parameterTypeMap = parameterTypeMap;
	}

	public Boolean getMethodSynchronized() {
		return methodSynchronized;
	}

	public void setMethodSynchronized(Boolean methodSynchronized) {
		this.methodSynchronized = methodSynchronized;
	}
	
}
