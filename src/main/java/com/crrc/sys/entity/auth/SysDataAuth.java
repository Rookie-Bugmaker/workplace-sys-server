package com.crrc.sys.entity.auth;

import com.crrc.sys.entity.BaseEntityYunLing;

import java.util.List;

/**
 * 数据权限
 *
 * @author 谭永强
 * @date 2023-03-22
 */
public class SysDataAuth extends BaseEntityYunLing {

	/**
	 * 主键
	 */
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
	/**
	 * 权限值集合
	 */
	private List<SysDataAuthValue> dataAuthValueList;
	/**
	 * 权限编码
	 */
	private String authCode;

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

	public List<SysDataAuthValue> getDataAuthValueList() {
		return dataAuthValueList;
	}

	public void setDataAuthValueList(List<SysDataAuthValue> dataAuthValueList) {
		this.dataAuthValueList = dataAuthValueList;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
}
