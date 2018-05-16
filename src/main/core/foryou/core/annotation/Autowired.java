package foryou.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
* @author 罗林 E-mail:1071893649@qq.com 
* @version 创建时间：2018年5月16日 上午10:25:01 
* 类说明 
*/
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.FIELD} )
@Documented
public @interface Autowired {

}
