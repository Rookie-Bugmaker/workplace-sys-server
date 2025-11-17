package com.crrc.sys.entity.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;

import java.util.List;

/**
 * 数据权限-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-17
 */
@TableName("sys_data_auth")
public class SysDataAuthDBEntity extends BaseEntityYunLing {

	/**
	 * 主键
	 */
	@TableId(type = IdType.INPUT)
	private Long authId;
	/**
	 * 权限类型(user:用户权限、role:角色权限)
	 */
	private String authType;
	/**
	 * 对象ID(用户ID或角色ID)
	 */
	private Long objectId;
	/**
	 * 配置文件ID
	 */
	private Long profileId;

	public Long getAuthId() {
		return authId;
	}

	public void setAuthId(Long authId) {
		this.authId = authId;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public Long getProfileId() {
		return profileId;
	}

	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}
}
