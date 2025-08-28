package com.yunling.sys.entity.role;

import com.yunling.sys.entity.BaseEntity;

/**
 * 组织权限关联表
 *
 * @author 谭永强
 * @date 2022-10-15
 */
public class SysRoleOrg extends BaseEntity {

    /**
     * 角色ID或用户ID
     */
    private Long roleId;
    /**
     * 组织ID
     */
    private Long orgId;

    public SysRoleOrg() {
    }

    public SysRoleOrg(Long roleId, Long orgId) {
        this.roleId = roleId;
        this.orgId = orgId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }
}
