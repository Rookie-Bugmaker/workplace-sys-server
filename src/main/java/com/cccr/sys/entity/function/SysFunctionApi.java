package com.cccr.sys.entity.function;

/**
 * @author 谭永强
 * @date 2024-07-13
 */
public class SysFunctionApi {

    /**
     * 功能ID
     */
    private Long functionId;
    /**
     * 接口ID
     */
    private Long apiId;
    /**
     * 接口名称
     */
    private String apiName;
    /**
     * 接口URI
     */
    private String apiUri;
    /**
     * 请求方式(GET/POST)
     */
    private String apiMethod;
    /**
     * 接口描述
     */
    private String apiDesc;

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiUri() {
        return apiUri;
    }

    public void setApiUri(String apiUri) {
        this.apiUri = apiUri;
    }

    public String getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(String apiMethod) {
        this.apiMethod = apiMethod;
    }

    public String getApiDesc() {
        return apiDesc;
    }

    public void setApiDesc(String apiDesc) {
        this.apiDesc = apiDesc;
    }
}
