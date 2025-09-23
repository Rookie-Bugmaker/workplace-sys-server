package com.crrc.sys.common.response;

import com.crrc.sys.common.ReturnCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseResult<T> {
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 提示信息，如果有错误时，前端可以获取该字段进行提示
     */
    private String msg;

    private Boolean success = false;
    /**
     * 查询到的结果数据，
     */
    private T data;

    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ResponseResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功返回结果
     *
     * @param data 返回的数据
     * @param <T>  泛型
     * @return 结果
     */
    public static <T> ResponseResult<T> success(T data) {
        ResponseResult<T> resultData = new ResponseResult<>();
        resultData.setCode(ReturnCode.RC200.getCode());
        resultData.setSuccess(true);
        resultData.setMsg(ReturnCode.RC200.getMessage());
        resultData.setData(data);
        return resultData;
    }
    /**
     * 错误返回
     *
     * @param code    状态码
     * @param message 消息
     * @param <T>     泛型
     * @return 结果
     */
    public static <T> ResponseResult<T> fail(int code, String message) {
        ResponseResult<T> resultData = new ResponseResult<>();
        resultData.setCode(code);
        resultData.setMsg(message);
        return resultData;
    }
}