package foryou.core.base;

import java.io.FileInputStream;
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
	 * ajax异步请求返回结果集
	 */
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
	public ThreadLocal<AjaxData> ajaxData = new ThreadLocal<AjaxData>();
	public ThreadLocal<AjaxBoolean> ajaxBoolean = new ThreadLocal<AjaxBoolean>();
	public ThreadLocal<RedirectData> redirectData = new ThreadLocal<RedirectData>();
	
	public ThreadLocal<ServletContext> servletContextThread = new ThreadLocal<ServletContext>();
	public ThreadLocal<HttpServletRequest> requestThread = new ThreadLocal<HttpServletRequest>();
	public ThreadLocal<HttpServletResponse> responseThread = new ThreadLocal<HttpServletResponse>();
	public ThreadLocal<HttpSession> sessionThread = new ThreadLocal<HttpSession>();
	public ThreadLocal<String> ipThread = new ThreadLocal<String>();
	
	public ThreadLocal<FileInputStream> fileInputStream;

	/**
	 * ajax Data值存入，有翻页
	 * 
	 * @param list
	 */
	public void setAjaxData(List<?> list) {
		AjaxData data = new AjaxData();
		data.setTotalProperty(list.size());
		data.setData(list);
		ajaxData.set(data);
	}

	/**
	 * ajax Data值存入，设置总记录数
	 * 
	 * @param list
	 * @param totalCount
	 */
	public void setAjaxData(List<?> list, long totalCount) {
		AjaxData data = new AjaxData();
		data.setData(list);
		data.setTotalProperty(totalCount);
		ajaxData.set(data);
	}

	/**
	 * ajax Boolean值存入
	 * 
	 * @param success
	 */
	public void setAjaxBoolean(Boolean success) {
		AjaxBoolean ajaxBooleandata = new AjaxBoolean();
		ajaxBooleandata.setSuccess(success);
		ajaxBoolean.set(ajaxBooleandata);
	}

	/**
	 * ajax Boolean值存入
	 * 
	 * @param success
	 * @param data
	 */
	public void setAjaxBoolean(Boolean success, Object data) {
		AjaxBoolean ajaxBooleandata = new AjaxBoolean();
		ajaxBooleandata.setSuccess(success);
		ajaxBooleandata.setData(data);
		ajaxBoolean.set(ajaxBooleandata);
	}

	/**
	 * ajax Boolean值存入
	 * 
	 * @param success
	 * @param msg
	 */
	public void setAjaxBoolean(Boolean success, String msg) {
		AjaxBoolean ajaxBooleandata = new AjaxBoolean();
		ajaxBooleandata.setSuccess(success);
		ajaxBooleandata.setMsg(msg);
		ajaxBoolean.set(ajaxBooleandata);
	}

	/**
	 * ajax Boolean值存入
	 * 
	 * @param success
	 * @param msg
	 * @param data
	 */
	public void setAjaxBoolean(Boolean success, String msg, Object data) {
		AjaxBoolean ajaxBooleandata = new AjaxBoolean();
		ajaxBooleandata.setSuccess(success);
		ajaxBooleandata.setMsg(msg);
		ajaxBooleandata.setData(data);
		ajaxBoolean.set(ajaxBooleandata);
	}

	/**
	 * 重定向结果集注入
	 * @param redirectType
	 * @param redirectUrl
	 */
	public void setRedirectData(String redirectUrl){
		RedirectData redirect = new RedirectData();
		redirect.setRedirectUrl(redirectUrl);
		redirectData.set(redirect);
	}
	
}
