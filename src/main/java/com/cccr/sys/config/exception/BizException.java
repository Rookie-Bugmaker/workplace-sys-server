package com.cccr.sys.config.exception;

import com.cccr.sys.common.ReturnCode;

/**
 * 自定义异常
 *
 * @author 谭永强
 * @date 2022-09-01
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 2133523721019975361L;
    /**
     * 错误码
     */
    private Integer errorCode;
    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 有参构造
     *
     * @param errorMsg 异常信息
     */
    public BizException(String errorMsg) {
        this.errorCode = ReturnCode.RC500.getCode();
        this.errorMsg = errorMsg;
    }

    /**
     * 有参构造
     *
     * @param errorCode 异常编码
     * @param errorMsg  异常信息
     */
    public BizException(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
