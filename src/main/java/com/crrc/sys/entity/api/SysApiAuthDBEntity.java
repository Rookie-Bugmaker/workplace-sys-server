package com.crrc.sys.entity.api;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;

/**
 * 权限(白/黑)名单-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-13
 */
@TableName("sys_api_auth")
public class SysApiAuthDBEntity extends BaseEntityYunLing {

    /**
     * 主键
     */
    @TableId(type = IdType.INPUT)
    private Long authId;
    /**
     * 权限URI(可使用通配符)
     */
    private String authUri;
    /**
     * 权限类型(A:白名单、B:黑名单)
     * 白名单：该API不需要权限校验。
     * 黑名单：该API禁止前端调用。
     */
    private String authType;
    /**
     * 描述
     */
    private String authDesc;

    public Long getAuthId() {
        return authId;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }

    public String getAuthUri() {
        return authUri;
    }

    public void setAuthUri(String authUri) {
        this.authUri = authUri;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getAuthDesc() {
        return authDesc;
    }

    public void setAuthDesc(String authDesc) {
        this.authDesc = authDesc;
    }
}
