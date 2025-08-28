package com.yunling.sys.entity.role;

import com.yunling.sys.entity.BaseEntity;

/**
 * 角色菜单
 *
 * @author 谭永强
 * @date 2022-09-22
 */
public class SysRoleMenu extends BaseEntity {

    /**
     * 角色ID
     */
    private Long roleId;
    /**
     * 菜单ID
     */
    private Long menuId;
    /**
     * 半勾选状态
     */
    private Boolean halfChecked;
	/**
	 * 是否是子菜单/功能ID(true/false:是/否)
	 */
	private Boolean childFlag = false;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Boolean getHalfChecked() {
        return halfChecked;
    }

    public void setHalfChecked(Boolean halfChecked) {
        this.halfChecked = halfChecked;
    }

	public Boolean getChildFlag() {
		return childFlag;
	}

	public void setChildFlag(Boolean childFlag) {
		this.childFlag = childFlag;
	}
}
