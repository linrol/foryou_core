package foryou.core.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import foryou.core.base.BaseController;
import foryou.core.entity.ControllerMethod;
import foryou.core.entity.ControllerPrototype;
import foryou.core.interceptor.Interceptor;
import foryou.core.mvc.MvcCore;

/**
 * 前端请求解析器
 * 
 * @author Administrator
 *
 */
public class RequestDispatcherFilter implements Filter {
	
	private Logger logger;

	public void init(FilterConfig filterConfig) throws ServletException {
		PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.foryou.core.properties"));
		logger = Logger.getLogger(this.getClass());
	}
	
	public void destroy() {

	}

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
		long startTime = System.currentTimeMillis();
		Long foryouCoreStartTime = 0l;
		Long foryouCoreEndTime = 0l;
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String[] paraseUrl;
		try {
			paraseUrl = MvcCore.paraseUrl(request.getRequestURL().toString(), MvcCore.CONTROLLER_PATTERN_KEY);
		} catch (Exception eCheckUrl) {
			eCheckUrl.printStackTrace();
			response.getWriter().write(eCheckUrl.getMessage());
			return;
		}
		logger.info("the request controller [" + paraseUrl[0] + "] and method [" + paraseUrl[1] + "] start");
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/json");
		response.setCharacterEncoding("UTF-8");
		try {
			String controllerName = paraseUrl[0];
			String methodName = paraseUrl[1];
			ControllerPrototype controllerPrototype = MvcCore.controllerPrototypeMap.get(controllerName);
			if (controllerPrototype == null) {
				logger.error("the request [" + request.getRequestURL() + "] not find controller");
				MvcCore.resultProcess("the request [" + request.getRequestURL() + "] not find controller", new BaseController(), request, response);
				return;
			}
			ControllerMethod controllerMethod = controllerPrototype.getMethodMap().get(methodName);
			if(controllerMethod == null || controllerMethod.getMethod() == null) {
				logger.error("the request [" + request.getRequestURL() + "] not find method");
				MvcCore.resultProcess("the request [" + request.getRequestURL() + "] not find method", new BaseController(), request, response);
				return;
			}
			//checkRepeatRequest(request.getSession());
			if (beforeInvoke(request, response, controllerPrototype)) { // 请求前拦截器调用
				return; 
			}
			Object controller = MvcCore.initController(controllerPrototype, request, response);
			foryouCoreEndTime = System.currentTimeMillis();
			String invokeResult = MvcCore.invokeMethod(controller, controllerMethod, request.getParameterMap());
			foryouCoreStartTime = System.currentTimeMillis();
			MvcCore.resultProcess(invokeResult, controller, request, response);
			if (afterInvoke(request, response, controllerPrototype)) { // 请求后拦截器调用
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			MvcCore.resultProcess(MvcCore.getExceptionAllinformation(e), new BaseController(), request, response);
			return;
		}
		long endTime = System.currentTimeMillis();
		logger.info("the request total time (" + (endTime - startTime) + ")ms,foryou_core run time(" + (foryouCoreEndTime + endTime - foryouCoreStartTime - startTime) + ")ms,business run time(" + (foryouCoreStartTime - foryouCoreEndTime) + ")ms");
	}

	/**
	 * 请求前拦截器调用
	 * 
	 * @param request
	 * @param response
	 * @param controllerName
	 * @return
	 * @throws Exception
	 */
	private boolean beforeInvoke(HttpServletRequest request, HttpServletResponse response, ControllerPrototype controllerPrototype) throws Exception {
		if (controllerPrototype.getInterceptorClassPath() ==  null) {
			return false;
		}
		for (String interceptorClassPath : controllerPrototype.getInterceptorClassPath().split(",")) {
			Interceptor interceptor = (Interceptor) Class.forName(interceptorClassPath).newInstance();
			if (!interceptor.beforeInvoke(request, response)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 请求后拦截器调用
	 * 
	 * @param request
	 * @param response
	 * @param controllerName
	 * @return
	 * @throws Exception
	 */
	private boolean afterInvoke(HttpServletRequest request, HttpServletResponse response, ControllerPrototype controllerPrototype) throws Exception {
		if (controllerPrototype.getInterceptorClassPath() ==  null) {
			return false;
		}
		String[] interceptorClassPaths = controllerPrototype.getInterceptorClassPath().split(",");
		for (int i = interceptorClassPaths.length - 1; i >= 0; i--) {
			Interceptor interceptor = (Interceptor) Class.forName(interceptorClassPaths[i]).newInstance();
			if (!interceptor.afterInvoke(request, response)) {
				return true;
			}
		}
		return false;
	}
}
