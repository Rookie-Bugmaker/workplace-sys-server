package com.yunling.sys.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 模糊查询中带有特殊字符的使用此注解自动转义
 *
 * @author huangsen
 * @date 2023/7/11 13:04
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableEscapeLikeSymbol {
}
