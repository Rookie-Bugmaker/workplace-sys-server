package com.crrc.sys.entity.user;

/**
 * 用户信息分页查询参数
 *
 * @author 谭永强
 * @date 2022-09-03
 */
public class SysUserFindByPageParam {

	/**
	 * 当前页码
	 */
	private Integer page;
	/**
	 * 偏移量，当pageSize=0时查询所有数据
	 */
	private Integer pageSize;
	/**
	 * 用户名(模糊搜索)
	 */
	private String userName;
	/**
	 * 用户描述(模糊搜索)
	 */
	private String userDescription;
	/**
	 * [用户名、用户描述]模糊搜索拼接条件(and | or)
	 */
	private String montage;
	/**
	 * 禁用状态(Y/N)
	 */
	private String disableState;
	/**
	 * 角色ID
	 */
	private Long roleId;

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserDescription() {
		return userDescription;
	}

	public void setUserDescription(String userDescription) {
		this.userDescription = userDescription;
	}

	public String getMontage() {
		return montage;
	}

	public void setMontage(String montage) {
		this.montage = montage;
	}

	public String getDisableState() {
		return disableState;
	}

	public void setDisableState(String disableState) {
		this.disableState = disableState;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
}
