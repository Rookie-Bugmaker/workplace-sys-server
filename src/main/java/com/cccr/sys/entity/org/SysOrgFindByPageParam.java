package com.cccr.sys.entity.org;

/**
 * 组织信息分页查询参数
 *
 * @author 谭永强
 * @date 2022-10-15
 */
public class SysOrgFindByPageParam {

	/**
	 * 当前页码
	 */
	private Integer page;
	/**
	 * 偏移量，当pageSize=0时查询所有数据
	 */
	private Integer pageSize;
	/**
	 * 语言(zh-CN:中文、en-US:英文)，
	 * 取值：LanguageEnum
	 */
	private String language;
	/**
	 * 组织编码(模糊搜索)
	 */
	private String orgCode;
	/**
	 * 组织名称(模糊搜索)
	 */
	private String orgName;
	/**
	 * 组织状态(Y/N:正常/禁用)
	 */
	private String orgStatus;

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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgStatus() {
		return orgStatus;
	}

	public void setOrgStatus(String orgStatus) {
		this.orgStatus = orgStatus;
	}
}
