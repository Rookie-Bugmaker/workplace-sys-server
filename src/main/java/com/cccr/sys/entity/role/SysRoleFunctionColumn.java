package com.cccr.sys.entity.role;

import com.cccr.sys.entity.BaseEntity;

/**
 * 角色列权限
 *
 * @author 谭永强
 * @date 2022-11-03
 */
public class SysRoleFunctionColumn extends BaseEntity {

    /**
     * 角色ID
     */
    private Long roleId;
    /**
     * 未拥有的列权限ID
     */
    private Long fcId;
	/**
	 * 可编辑(Y:是、N:否)
	 */
	private String fcEditableFlag;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getFcId() {
        return fcId;
    }

    public void setFcId(Long fcId) {
        this.fcId = fcId;
    }

	public String getFcEditableFlag() {
		return fcEditableFlag;
	}

	public void setFcEditableFlag(String fcEditableFlag) {
		this.fcEditableFlag = fcEditableFlag;
	}
}
