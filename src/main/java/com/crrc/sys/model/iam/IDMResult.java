package com.crrc.sys.model.iam;

import lombok.Data;

public class IDMResult {
    private String code;
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public IDMResult(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static IDMResult success(String msg) {
        return new IDMResult("0", msg);
    }

    public static IDMResult fail(String msg) {
        return new IDMResult("-1", msg);
    }
}
