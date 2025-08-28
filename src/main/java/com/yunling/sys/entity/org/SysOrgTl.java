package com.yunling.sys.entity.org;

import com.yunling.sys.entity.BaseEntity;

/**
 * 组织-多语言
 * @author 谭永强
 * @date 2022-10-15
 */
public class SysOrgTl extends BaseEntity {

    /**
     * 组织ID
     */
    private Long orgId;
    /**
     * 语言(zh-CN:中文、en-US:英文)，
     * 取值：LanguageEnum
     */
    private String language;
    /**
     * 组织名称
     */
    private String orgName;
    /**
     * 组织地点
     */
    private String orgLocation;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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
}
