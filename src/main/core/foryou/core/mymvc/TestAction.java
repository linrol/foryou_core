package foryou.core.mymvc;

import foryou.core.annotation.ClassMapingUrl;
import foryou.core.base.BaseController;

/** 
* @author 罗林 E-mail:1071893649@qq.com 
* @version 创建时间：2018年5月13日 下午7:17:12 
* 类说明 
*/
@ClassMapingUrl
public class TestAction extends BaseController {
	
	private Integer a;
	
	private int b;

	public String getShareList(String[] searchKeys,String[] searchValues,int acb,Integer bca) {
		try {
			System.out.println(searchKeys + "....." + searchValues + "..." + acb + "..." + bca + ".." + a + ".."+b);
			return RESULT_AJAX_DATA;
		} catch (Exception e) {
			e.printStackTrace();
			setAjaxBoolean(false, e.getMessage());
			return RESULT_AJAX_BOOLEAN;
		}
	}
}
