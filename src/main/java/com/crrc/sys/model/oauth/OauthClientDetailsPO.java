package com.crrc.sys.model.oauth;

import com.crrc.sys.entity.BaseEntityYunLing;

/**
 * 客户端信息
 *
 * @author 谭永强
 * @date 2024-12-06
 */
public class OauthClientDetailsPO extends BaseEntityYunLing {

    /**
     * 用户关联ID
     */
    private Long id;
    /**
     * 客户端ID
     */
    private String clientId;
    /**
     * 客户端密码(Bcrypt密文)
     */
    private String clientSecret;
    /**
     * 原密码
     */
    private String originalSecret;
    /**
     * 客户端名称
     */
    private String clientName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getOriginalSecret() {
        return originalSecret;
    }

    public void setOriginalSecret(String originalSecret) {
        this.originalSecret = originalSecret;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
