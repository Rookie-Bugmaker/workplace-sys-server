package com.cccr.sys.entity.role;

import com.cccr.sys.entity.BaseEntity;

/**
 * 角色菜单列值集
 *
 * @author 谭永强
 * @date 2022-10-21
 */
public class SysRoleColumnValueSet extends BaseEntity {

	/**
	 * 角色ID
	 */
	private Long roleId;
	/**
	 * 块ID
	 */
	private Long blockId;
	/**
	 * 列权限ID
	 */
	private Long fcId;
	/**
	 * 值集ID
	 */
	private Long vsId;
	/**
	 * 列权限编码
	 */
	private String fcCode;
	/**
	 * 值集编码
	 */
	private String vsCode;

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getBlockId() {
		return blockId;
	}

	public void setBlockId(Long blockId) {
		this.blockId = blockId;
	}

	public Long getFcId() {
		return fcId;
	}

	public void setFcId(Long fcId) {
		this.fcId = fcId;
	}

	public Long getVsId() {
		return vsId;
	}

	public void setVsId(Long vsId) {
		this.vsId = vsId;
	}

	public String getFcCode() {
		return fcCode;
	}

	public void setFcCode(String fcCode) {
		this.fcCode = fcCode;
	}

	public String getVsCode() {
		return vsCode;
	}

	public void setVsCode(String vsCode) {
		this.vsCode = vsCode;
	}
}
