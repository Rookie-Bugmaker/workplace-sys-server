package com.crrc.sys.entity.profile;

import com.crrc.sys.entity.BaseEntityYunLing;

/**
 * @author 谭永强
 * @date 2022-12-20
 */
public class SysProfileTl extends BaseEntityYunLing {

	/**
	 * 主键
	 */
	private Long profileId;
	/**
	 * 语言(zh-CN:中文、en-US:英文)，
	 * 取值：LanguageEnum
	 */
	private String language;
	/**
	 * 配置名称
	 */
	private String profileName;

	public Long getProfileId() {
		return profileId;
	}

	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
}
