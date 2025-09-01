package com.crrc.sys.entity.homepage;

import com.crrc.sys.entity.BaseEntityYunLing;

/**
 * <p>
 * 主页组件定义表
 * </p>
 *
 * @author fucheng
 * @since 2023-05-19
 */
public class SysHomePageComponent extends BaseEntityYunLing {

    /**
     * 主键id
     */
    private Long componentId;

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
     * 功能编码
     */
    private String functionCode;

    /**
     * 刷新频率（秒）
     */
    private Integer refreshRate;

    /**
     * 加载限制类型 1 用户类型 2 用户
     */
    private Integer loadingType;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 状态 Y 有效/ N 无效
     */
    private String status;

    /**
     * 显示状态 Y 是/ N 否
     */
    private String showStatus;

    /**
     * 功能路径
     */
    private String functionUrl;

	/**
	 * 业务领域名称
	 */
	private String businessAreaName;

	public String getBusinessAreaName() {
		return businessAreaName;
	}

	public void setBusinessAreaName(String businessAreaName) {
		this.businessAreaName = businessAreaName;
	}

	public Long getComponentId() {
		return componentId;
	}

	public void setComponentId(Long componentId) {
		this.componentId = componentId;
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

	public String getFunctionCode() {
		return functionCode;
	}

	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}

	public Integer getRefreshRate() {
		return refreshRate;
	}

	public void setRefreshRate(Integer refreshRate) {
		this.refreshRate = refreshRate;
	}

	public Integer getLoadingType() {
		return loadingType;
	}

	public void setLoadingType(Integer loadingType) {
		this.loadingType = loadingType;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getShowStatus() {
		return showStatus;
	}

	public void setShowStatus(String showStatus) {
		this.showStatus = showStatus;
	}

	public String getFunctionUrl() {
		return functionUrl;
	}

	public void setFunctionUrl(String functionUrl) {
		this.functionUrl = functionUrl;
	}
}
