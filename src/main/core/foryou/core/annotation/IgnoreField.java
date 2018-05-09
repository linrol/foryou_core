package foryou.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述field被忽略，不进行属性注入操作,作用域可以是class或者field
 * class作用域标明需要忽略的fieldName
 * field作用域时无需标明
 * 
 * @author luolin
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.FIELD} )
@Documented
public @interface IgnoreField {
	public String fieldNames();
}
