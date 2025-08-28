package com.yunling.sys.entity.api;

import com.yunling.sys.entity.BaseEntity;

/**
 * API实体
 *
 * @author 谭永强
 * @date 2022-09-13
 */
public class SysApi extends BaseEntity {

    /**
     * 主键
     */
    private Long id;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
