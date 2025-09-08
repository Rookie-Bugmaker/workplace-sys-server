package com.crrc.sys.common;

import java.util.HashMap;
import java.util.Map;

public class MongoResResult {
    private int code;
    private String msg;
    private Object data;

    public MongoResResult(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static MongoResResult success() {
        return new MongoResResult(200, "操作成功", null);
    }

    public static MongoResResult success(String msg) {
        return new MongoResResult(200, msg, null);
    }

    public static MongoResResult success(Object data) {
        return new MongoResResult(200, "操作成功", data);
    }

    public static MongoResResult success(String msg, Object data) {
        return new MongoResResult(200, msg, data);
    }

    public static MongoResResult error() {
        return new MongoResResult(500, "操作失败", null);
    }

    public static MongoResResult error(String msg) {
        return new MongoResResult(500, msg, null);
    }

    public static MongoResResult error(String msg, Object data) {
        return new MongoResResult(500, msg, data);
    }

    public static MongoResResult error(int code, String msg) {
        return new MongoResResult(code, msg, null);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("msg", msg);
        map.put("data", data);
        return map;
    }

    // getters and setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}