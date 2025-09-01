package com.cccr.sys.entity.function.column;

import com.cccr.sys.entity.BaseEntity;

/**
 * 列权限-多语言
 *
 * @author 谭永强
 * @date 2022-10-31
 */
public class SysFunctionColumnTl extends BaseEntity {

    /**
     * 主键
     */
    private Long fcId;
    /**
     * (块/列)编码
     */
    private String fcCode;
    /**
     * (块/列)名称
     */
    private String fcName;
	/**
	 * (块/列)描述
	 */
	private String fcDesc;
	/**
	 * 语言:LanguageEnum
	 * zh-CN:中文、en-US:英文
	 */
	private String language;

	public Long getFcId() {
		return fcId;
	}

	public void setFcId(Long fcId) {
		this.fcId = fcId;
	}

	public String getFcCode() {
		return fcCode;
	}

	public void setFcCode(String fcCode) {
		this.fcCode = fcCode;
	}

	public String getFcName() {
		return fcName;
	}

	public void setFcName(String fcName) {
		this.fcName = fcName;
	}

	public String getFcDesc() {
		return fcDesc;
	}

	public void setFcDesc(String fcDesc) {
		this.fcDesc = fcDesc;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
