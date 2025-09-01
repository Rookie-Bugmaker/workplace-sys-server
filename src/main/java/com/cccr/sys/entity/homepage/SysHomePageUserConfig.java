package com.cccr.sys.entity.homepage;

import com.cccr.sys.entity.BaseEntity;

/**
 * 1 * @Author: fucheng
 * 2 * @Description:
 * 3 * @Date: 2023/5/22
 */
public class SysHomePageUserConfig extends BaseEntity {
	/**
	 * 主键id
	 */
	private Long configId;
	/**
	 * 用户id
	 */
	private Long userId;
	/**
	 * 用户配置
	 */
	private String userConfig;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getConfigId() {
		return configId;
	}

	public void setConfigId(Long configId) {
		this.configId = configId;
	}

	public String getUserConfig() {
		return userConfig;
	}

	public void setUserConfig(String userConfig) {
		this.userConfig = userConfig;
	}
}
