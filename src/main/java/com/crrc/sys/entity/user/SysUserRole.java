package com.crrc.sys.entity.user;

import com.crrc.sys.entity.BaseEntityYunLing;

/**
 * 用户角色中间表
 *
 * @author 谭永强
 * @date 2022-09-09
 */
public class SysUserRole extends BaseEntityYunLing {

    /**
     * 主键
     */
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 角色ID
     */
    private Long roleId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
