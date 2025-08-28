package com.yunling.sys.entity.homepage;

import com.yunling.sys.entity.BaseEntity;

import java.util.List;

/**
 * 1 * @Author: fucheng
 * 2 * @Description:
 * 3 * @Date: 2023/5/19
 */
public class SysHomePageComponentUser extends BaseEntity {
	/*
	  主键
	 */
	private Long componentUserId;

	/**
	 * 组件id
	 */
	private Long componentId;

	/**
	 * 用户id
	 */
	private Long userId;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getComponentUserId() {
		return componentUserId;
	}

	public void setComponentUserId(Long componentUserId) {
		this.componentUserId = componentUserId;
	}

	public Long getComponentId() {
		return componentId;
	}

	public void setComponentId(Long componentId) {
		this.componentId = componentId;
	}

}
