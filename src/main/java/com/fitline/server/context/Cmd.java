package com.fitline.server.context;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Cmd {

	/**
	 * 指令版本
	 * @return
	 */
	int version() default 0;
	/**
	 * 指令值
	 * @return
	 */
	int value();
	/**
	 * 是否异步
	 * @return
	 */
	boolean async() default true;
}
