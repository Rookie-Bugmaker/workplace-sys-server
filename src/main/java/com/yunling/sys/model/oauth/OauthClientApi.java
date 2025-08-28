package com.yunling.sys.model.oauth;

import com.yunling.sys.entity.BaseEntity;

import java.util.Date;

/**
 * 客户端权限关联表
 *
 * @author 谭永强
 * @date 2024-12-06
 */
public class OauthClientApi extends BaseEntity {

    /**
     * 客户端ID
     */
    private String clientId;
    /**
     * 接口ID
     */
    private Long apiId;

    public OauthClientApi() {
    }

    public OauthClientApi(String clientId, Long apiId, Long createBy, Date createDate) {
        this.clientId = clientId;
        this.apiId = apiId;
        super.setCreateBy(createBy);
        super.setCreateDate(createDate);
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }
}
