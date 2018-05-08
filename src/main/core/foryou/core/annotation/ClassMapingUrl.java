package foryou.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述class所在url位置,定义Action时注解此Annotation即可实现前端到Action的访问
 * 
 * @author luolin
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ClassMapingUrl {
	public String value() default "CLASS_NAME";
}
