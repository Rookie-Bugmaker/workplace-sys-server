package com.crrc.sys.model.oauth;

import java.util.List;

/**
 * @author 谭永强
 * @date 2024-12-06
 */
public class OauthClientAuthorizeParam {

    /**
     * 客户端ID
     */
    private String clientId;
    /**
     * 权限列表
     */
    private List<Long> apiIdList;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<Long> getApiIdList() {
        return apiIdList;
    }

    public void setApiIdList(List<Long> apiIdList) {
        this.apiIdList = apiIdList;
    }
}
