package com.cccr.sys.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义字段描述注解
 * 该注解用于定义接口参数的字段名称
 *
 * @author 谭永强
 * @date 2024-10-11
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyFieldDescription {

    /**
     * 字段名称
     *
     * @return 字段名称
     */
    String value() default "";
}
