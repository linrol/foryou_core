package foryou.core.common;

/**
 * Ajax返回Boolean
 * 
 * @author 罗林
 *
 */
public class AjaxBoolean {

	private boolean success = true;
	private String msg;
	private Object data;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
