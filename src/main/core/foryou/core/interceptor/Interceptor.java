package foryou.core.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 罗林 E-mail:1071893649@qq.com
 * @version 创建时间：2017年10月23日 下午8:40:40 类说明
 */
public interface Interceptor {

	/**
	 * 请求前调用
	 * @param request
	 * @param response
	 * @return true继续调用业务逻辑，false终结请求返回response
	 * @throws Exception
	 */
	public Boolean beforeInvoke(HttpServletRequest request, HttpServletResponse response) throws Exception;

	/**
	 * 请求后调用
	 * @param request
	 * @param response
	 * @return true继续调用业务逻辑，false终结请求返回response
	 * @throws Exception
	 */
	public Boolean afterInvoke(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
