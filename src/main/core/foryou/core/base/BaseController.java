package foryou.core.base;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import foryou.core.common.AjaxBoolean;
import foryou.core.common.AjaxData;
import foryou.core.common.Paginate;
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
	public AjaxData ajaxData = new AjaxData();
	public AjaxBoolean ajaxBoolean = new AjaxBoolean();
	public RedirectData redirectData = new RedirectData();
	
	public ServletContext servletContext;
	public HttpServletRequest request;
	public HttpServletResponse response;
	public HttpSession session;
	public String ip;

	/**
	 * 分页组件
	 */
	public Paginate paginate = new Paginate();
	public FileInputStream fileInputStream;

	protected String searchKeyValue = "";
	protected String searchKey = "";

	/**
	 * ajax Data值存入，有翻页
	 * 
	 * @param list 入参
	 */
	public void setAjaxData(List<?> list) {
		List<Object> paginData = new ArrayList<Object>();
		for (int i = paginate.getStart(); i < paginate.getStart() + paginate.getLimit(); i++) {
			try {
				paginData.add(list.get(i));
			} catch (Exception e) {
				break;
			}
		}
		ajaxData.setTotalProperty(list.size());
		ajaxData.setData(paginData);
	}

	/**
	 * ajax Data值存入，是否需要翻页
	 * 
	 * @param list 入参
	 * @param page 是否需要翻页
	 */
	public void setAjaxData(List<?> list, boolean page) {
		List<Object> paginData = new ArrayList<Object>();
		if (page) {
			for (int i = paginate.getStart(); i < paginate.getStart() + paginate.getLimit(); i++) {
				try {
					paginData.add(list.get(i));
				} catch (Exception e) {
					break;
				}
			}
			ajaxData.setData(paginData);
		} else {
			ajaxData.setData(list);
		}
		ajaxData.setTotalProperty(list.size());
	}

	/**
	 * ajax Data值存入，设置总记录数
	 * 
	 * @param list 入参
	 * @param totalCount 总数量
	 */
	public void setAjaxData(List<?> list, long totalCount) {
		ajaxData.setData(list);
		ajaxData.setTotalProperty(totalCount);
	}

	/**
	 * ajax Boolean值存入
	 * 
	 * @param success 是否成功
	 */
	public void setAjaxBoolean(Boolean success) {
		ajaxBoolean.setSuccess(success);
	}

	/**
	 * ajax Boolean值存入
	 * 
	 * @param success 是否成功
	 * @param data 数据
	 */
	public void setAjaxBoolean(Boolean success, Object data) {
		ajaxBoolean.setSuccess(success);
		ajaxBoolean.setData(data);
	}

	/**
	 * ajax Boolean值存入
	 * 
	 * @param success 是否成功
	 * @param msg 提示消息
	 */
	public void setAjaxBoolean(Boolean success, String msg) {
		ajaxBoolean.setSuccess(success);
		ajaxBoolean.setMsg(msg);
	}

	/**
	 * ajax Boolean值存入
	 * 
	 * @param success 是否成功
	 * @param msg 提示小时
	 * @param data 数据
	 */
	public void setAjaxBoolean(Boolean success, String msg, Object data) {
		ajaxBoolean.setSuccess(success);
		ajaxBoolean.setMsg(msg);
		ajaxBoolean.setData(data);
	}

	/**
	 * 重定向结果集
	 * @param redirectUrl 跳转url
	 */
	public void setRedirectData(String redirectUrl){
		redirectData.setRedirectUrl(redirectUrl);
	}
	
}
