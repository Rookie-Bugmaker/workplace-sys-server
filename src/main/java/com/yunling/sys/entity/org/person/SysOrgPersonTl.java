package com.yunling.sys.entity.org.person;

import com.yunling.sys.entity.BaseEntity;

/**
 * @author 谭永强
 * @date 2022-12-09
 */
public class SysOrgPersonTl extends BaseEntity {

	/**
	 * 人员ID
	 */
	private Long personId;
	/**
	 * 姓名
	 */
	private String fullName;
	/**
	 * 语言(zh-CN:中文、en-US:英文)，
	 * 取值：LanguageEnum
	 */
	private String language;

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
