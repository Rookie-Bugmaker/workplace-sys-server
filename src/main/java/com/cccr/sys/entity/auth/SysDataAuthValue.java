package com.cccr.sys.entity.auth;

import com.cccr.sys.entity.BaseEntity;

/**
 * @author 谭永强
 * @date 2023-03-22
 */
public class SysDataAuthValue extends BaseEntity {

    /**
     * 数据权限ID
     */
    private Long authId;
    /**
     * 权限值
     */
    private String authValue;
    /**
     * 权限值描述
     */
    private String authValueDesc;

    public Long getAuthId() {
        return authId;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }

    public String getAuthValue() {
        return authValue;
    }

    public void setAuthValue(String authValue) {
        this.authValue = authValue;
    }

    public String getAuthValueDesc() {
        return authValueDesc;
    }

    public void setAuthValueDesc(String authValueDesc) {
        this.authValueDesc = authValueDesc;
    }
}
