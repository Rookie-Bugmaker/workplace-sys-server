package com.crrc.sys.annotation;

import com.crrc.sys.enums.LogType;
import com.crrc.sys.enums.OperateType;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {

    /**
     * 方法名称
     */
    String value() default "";

    /**
     * 日志类型
     */
    LogType logType() default LogType.TYPE_OPERATE;

    /**
     * 操作类型
     */
    OperateType operateType() default OperateType.TYPE_LOGIN;

    /**
     * 业务单据编码
     */
    String businessCode() default "";

    /**
     * 业务单据名称
     */
    String businessName() default "";
}