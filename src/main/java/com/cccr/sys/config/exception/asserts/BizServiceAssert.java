package com.cccr.sys.config.exception.asserts;


import com.cccr.sys.config.exception.BizException;
import com.cccr.sys.config.exception.ParamValidateException;
import org.springframework.lang.Nullable;

import java.util.function.Supplier;

/**
 * 业务异常服务工具
 *
 * @author huangsen
 * @date 2022/12/5 13:33
 */
public abstract class BizServiceAssert {

    private BizServiceAssert() {
    }

    /**
     * @param expression 表达式
     * @param message    业务异常信息
     */
    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new BizException(message);
        }
    }


    /**
     * @param message 异常信息
     */
    public static void throwExp(String message) {
        throw new BizException(message);
    }

    /**
     * @param expression 表达式
     * @param bizCode    业务代码
     * @param message    业务异常信息
     */
    public static void state(boolean expression, Integer bizCode, String message) {
        if (!expression) {
            throw new BizException(bizCode, message);
        }
    }

    /**
     * @param expression 表达式
     * @param message    业务异常信息
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new BizException(message);
        }
    }

    /**
     * @param expression      表达式
     * @param messageSupplier 信息提供函数
     */
    public static void isTrue(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new BizException(nullSafeGet(messageSupplier));
        }
    }

    /**
     * @param messageSupplier 信息提供函数
     */
    @Nullable
    private static String nullSafeGet(@Nullable Supplier<String> messageSupplier) {
        return messageSupplier != null ? (String) messageSupplier.get() : null;
    }

    /**
     * 校验字段
     *
     * @param expression 表达式
     * @param message    业务异常信息
     */
    public static void validate(boolean expression, String message) {
        if (!expression) {
            throw new ParamValidateException(message);
        }
    }
}
