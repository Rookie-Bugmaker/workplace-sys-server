package com.yunling.sys.model.oauth;

import java.util.Date;

/**
 * @author 谭永强
 * @date 2024-12-06
 */
public class OauthClientDetailsVO {

    /**
     * 用户关联ID
     */
    private Long id;
    /**
     * 客户端ID
     */
    private String clientId;
    /**
     * 原密码
     */
    private String originalSecret;
    /**
     * 客户端名称
     */
    private String clientName;
    /**
     * 创建人名称
     */
    private String createByName;
    /**
     * 创建时间
     */
    private Date createDate;

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

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "OauthClientDetailsVO{" +
                "id=" + id +
                ", clientId='" + clientId + '\'' +
                ", originalSecret='" + originalSecret + '\'' +
                ", clientName='" + clientName + '\'' +
                ", createByName='" + createByName + '\'' +
                ", createDate=" + createDate +
                '}';
    }
}
