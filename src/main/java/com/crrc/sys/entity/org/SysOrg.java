package com.crrc.sys.entity.org;

import com.crrc.sys.entity.BaseEntity;

/**
 * 组织
 * @author 谭永强
 * @date 2022-10-15
 */
public class SysOrg extends BaseEntity {

    /**
     * 组织ID
     */
    private Long orgId;
    /**
     * 组织编码
     */
    private String orgCode;
    /**
     * 组织状态
     */
    private String orgStatus;
    /**
     * 组织名称
     */
    private String orgName;
    /**
     * 组织地点
     */
    private String orgLocation;
    /**
     * 语言(zh-CN:中文、en-US:英文)，
     * 取值：LanguageEnum
     */
    private String language;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgStatus() {
        return orgStatus;
    }

    public void setOrgStatus(String orgStatus) {
        this.orgStatus = orgStatus;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgLocation() {
        return orgLocation;
    }

    public void setOrgLocation(String orgLocation) {
        this.orgLocation = orgLocation;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
