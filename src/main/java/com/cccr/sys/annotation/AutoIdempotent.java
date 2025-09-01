package com.cccr.sys.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动幂等
 * 该注解加在需要幂等的方法上，即可自动上线方法的幂等。
 *
 * @author 谭永强
 * @date 2023-08-15
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoIdempotent {

    /**
     * 限定时间(秒)
     * 限制多少秒内，每个用户只能请求一次该接口。
     */
    long seconds() default 1;
}
