package foryou.core.base;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import foryou.core.common.AjaxBoolean;
import foryou.core.common.AjaxData;
import foryou.core.common.RedirectData;

/**
 * @author 罗林 E-mail:1071893649@qq.com
 * @version 创建时间：2017年6月20日 下午2:49:27 类说明
 */
public class BaseController {

	/**
	 * ajax list结果集返回
	 */
	public static final String RESULT_AJAX_DATA = "ajax_data";
	/**
	 * ajax 布尔类型集返回
	 */
	public static final String RESULT_AJAX_BOOLEAN = "ajax_boolean";
	/**
	 * error结果集的返回
	 */
	public static final String RESULT_AJAX_ERROR = "ajax_error";
	/**
	 * ajax 登录结果处理
	 */
	public static final String RESULT_AJAX_STREAM = "ajax_strem";
	/**
	 * 重定向结果集
	 */
	public static final String RESULT_REDIRECT = "redirect";
	
	/**
	 * 返回的Json数据类型格式
	 */
	public ThreadLocal<AjaxData> ajaxDataThreadLocal = new ThreadLocal<AjaxData>();
	public ThreadLocal<AjaxBoolean> ajaxBooleanThreadLocal = new ThreadLocal<AjaxBoolean>();
	public ThreadLocal<RedirectData> redirectDataThreadLocal = new ThreadLocal<RedirectData>();
		
	public ThreadLocal<ServletContext>  servletContextThreadLocal = new ThreadLocal<ServletContext>();
	public ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<HttpServletRequest>();
	public ThreadLocal<HttpServletResponse> responseThreadLocal = new ThreadLocal<HttpServletResponse>();
	public ThreadLocal<HttpSession> sessionThreadLocal = new ThreadLocal<HttpSession>();
	public ThreadLocal<String> ipThreadLocal = new ThreadLocal<String>();

	/**
	 * ajax Data值存入，有翻页
	 * 
	 * @param list 入参
	 */
	public void setAjaxData(List<?> list) {
		AjaxData ajaxData = new AjaxData();
		ajaxData.setTotalProperty(list.size());
		ajaxData.setData(list);
		ajaxDataThreadLocal.set(ajaxData);
	}


	/**
	 * ajax Data值存入，设置总记录数
	 * 
	 * @param list 入参
	 * @param totalCount 总数量
	 */
	public void setAjaxData(List<?> list, long totalCount) {
		AjaxData ajaxData = new AjaxData();
		ajaxData.setData(list);
		ajaxData.setTotalProperty(totalCount);
		ajaxDataThreadLocal.set(ajaxData);
	}

	/**
	 * ajax Boolean值存入
	 * 
	 * @param success 是否成功
	 */
	public void setAjaxBoolean(Boolean success) {
		AjaxBoolean ajaxBoolean = new AjaxBoolean();
		ajaxBoolean.setSuccess(success);
		ajaxBooleanThreadLocal.set(ajaxBoolean);
	}

	/**
	 * ajax Boolean值存入
	 * 
	 * @param success 是否成功
	 * @param data 数据
	 */
	public void setAjaxBoolean(Boolean success, Object data) {
		AjaxBoolean ajaxBoolean = new AjaxBoolean();
		ajaxBoolean.setSuccess(success);
		ajaxBoolean.setData(data);
		ajaxBooleanThreadLocal.set(ajaxBoolean);
	}

	/**
	 * ajax Boolean值存入
	 * 
	 * @param success 是否成功
	 * @param msg 提示消息
	 */
	public void setAjaxBoolean(Boolean success, String msg) {
		AjaxBoolean ajaxBoolean = new AjaxBoolean();
		ajaxBoolean.setSuccess(success);
		ajaxBoolean.setMsg(msg);
		ajaxBooleanThreadLocal.set(ajaxBoolean);
	}

	/**
	 * ajax Boolean值存入
	 * 
	 * @param success 是否成功
	 * @param msg 提示小时
	 * @param data 数据
	 */
	public void setAjaxBoolean(Boolean success, String msg, Object data) {
		AjaxBoolean ajaxBoolean = new AjaxBoolean();
		ajaxBoolean.setSuccess(success);
		ajaxBoolean.setMsg(msg);
		ajaxBoolean.setData(data);
		ajaxBooleanThreadLocal.set(ajaxBoolean);
	}

	/**
	 * 重定向结果集
	 * @param redirectUrl 跳转url
	 */
	public void setRedirectData(String redirectUrl){
		RedirectData redirectData = new RedirectData();
		redirectData.setRedirectUrl(redirectUrl);
		redirectDataThreadLocal.set(redirectData);
	}
	
}
