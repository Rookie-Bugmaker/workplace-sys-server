package com.crrc.sys.entity.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;

/**
 * 数据权限值-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-17
 */
@TableName("sys_data_auth_value")
public class SysDataAuthValueDBEntity extends BaseEntityYunLing{

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
