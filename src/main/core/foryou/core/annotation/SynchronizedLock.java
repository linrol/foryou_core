package foryou.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
* @author 罗林 E-mail:1071893649@qq.com 
* @version 创建时间：2018年1月13日 下午6:09:32 
* 类锁，使用范围为method，效果为
* synchronized(Controller.class) {
* } 
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface SynchronizedLock {
	public String value() default "class";
}
