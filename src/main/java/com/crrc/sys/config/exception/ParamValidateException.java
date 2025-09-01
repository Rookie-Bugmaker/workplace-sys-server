package com.crrc.sys.config.exception;

import com.crrc.sys.common.ReturnCode;

/**
 * 自定义参数校验异常
 * @author 谭永强
 * @date 2022-09-01
 */
public class ParamValidateException extends RuntimeException {

    private static final long serialVersionUID = 6559777039305628048L;
    /**
     * 错误码
     */
    private Integer errorCode;
    /**
     * 错误信息
     */
    private String errorMsg;

    public ParamValidateException(String errorMsg) {
        this.errorCode = ReturnCode.RC205.getCode();
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