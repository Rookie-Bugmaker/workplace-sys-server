package com.crrc.sys.model.org.dept;

import com.crrc.sys.entity.BaseEntity;

/**
 * 部门组织
 *
 * @author 谭永强
 * @date 2024-09-03
 */
public class SysOrgDeptOrgPO extends BaseEntity {

    /**
     * 主键
     */
    private Long id;
    /**
     * 部门ID
     */
    private Long deptId;
    /**
     * 组织ID
     */
    private Long orgId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }
}
