package foryou.core.common;

import java.util.List;

/**
 * 
 * 
 * @author 罗林
 * 
 */
public class AjaxData {
	public long totalProperty;
	public List<?> data;
	public byte[] byteData;

	public long getTotalProperty() {
		return totalProperty;
	}

	public void setTotalProperty(long totalProperty) {
		this.totalProperty = totalProperty;
	}

	public List<?> getData() {
		return data;
	}

	public void setData(List<?> data) {
		this.data = data;
	}

	public byte[] getByteData() {
		return byteData;
	}

	public void setByteData(byte[] byteData) {
		this.byteData = byteData;
	}

}
