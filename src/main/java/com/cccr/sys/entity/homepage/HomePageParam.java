package com.cccr.sys.entity.homepage;

/**
 * 1 * @Author: fucheng
 * 2 * @Description:
 * 3 * @Date: 2023/5/19
 */
public class HomePageParam {

	/**
	 * 主页标题
	 */
	private String homePageTitle;

	/**
	 * 功能名称
	 */
	private String functionName;

	/**
	 * 业务领域id
	 */
	private Long businessAreaId;

	/**
	 * 状态 Y 有效/ N 无效
	 */
	private String status;

	/**
	 * 当前页
	 */
	private Integer page;

	/**
	 * 页面大小
	 */
	private Integer pageSize;

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

	public String getHomePageTitle() {
		return homePageTitle;
	}

	public void setHomePageTitle(String homePageTitle) {
		this.homePageTitle = homePageTitle;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public Long getBusinessAreaId() {
		return businessAreaId;
	}

	public void setBusinessAreaId(Long businessAreaId) {
		this.businessAreaId = businessAreaId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
