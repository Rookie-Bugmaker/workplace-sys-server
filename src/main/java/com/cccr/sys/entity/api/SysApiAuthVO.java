package com.cccr.sys.entity.api;

/**
 * 导出/导入实体
 *
 * @author 谭永强
 * @date 2024-01-05
 */
public class SysApiAuthVO {

    /**
     * 主键
     */
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

    public SysApiAuthVO() {
    }

    public SysApiAuthVO(Long authId, String authUri, String authType, String authDesc) {
        this.authId = authId;
        this.authUri = authUri;
        this.authType = authType;
        this.authDesc = authDesc;
    }

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
