package com.crrc.sys.entity.homepage;

import com.crrc.sys.entity.BaseEntity;

/**
 * 1 * @Author: fucheng
 * 2 * @Description:
 * 3 * @Date: 2023/5/19
 */
public class SysHomePageComponentUserParam extends BaseEntity {
	/**
	 * 主键
 	*/
	private Long componentUserId;

	/**
	 * 组件id
	 */
	private Long componentId;

	/**
	 * 用户id
	 */
	private String userIds;

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

	public String getUserIds() {
		return userIds;
	}

	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}
}
