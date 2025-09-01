package com.crrc.sys.model.valueset;

import com.crrc.sys.entity.BaseEntity;

/**
 * @author 谭永强
 * @date 2024-12-23
 */
public class SysValueSetOrgPO extends BaseEntity {

    /**
     * 主键
     */
    private Long id;
    /**
     * 值集行ID,组织只绑定在值集行上
     */
    private Long vsId;
    /**
     * 组织ID
     */
    private Long organizationId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVsId() {
        return vsId;
    }

    public void setVsId(Long vsId) {
        this.vsId = vsId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
