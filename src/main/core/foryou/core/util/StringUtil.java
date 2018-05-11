package foryou.core.util;
/** 
* @author 罗林 E-mail:1071893649@qq.com 
* @version 创建时间：2017年12月18日 下午2:45:14 
* 类说明 
*/
public class StringUtil {
	
	public static boolean isEmpty(CharSequence cs) {
		return (cs == null) || (cs.length() == 0);
	}
	
	public static boolean isNotEmpty(CharSequence cs) {
		return !isEmpty(cs);
	}
	
	public static boolean equalsOr(String from,String... tos) {
		for(String to : tos) {
			if (from.equals(to)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean equalsNotOr(String from,String... tos) {
		return !equalsOr(from, tos);
	}
}