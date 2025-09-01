package com.cccr.sys.entity.profile;

import com.cccr.sys.entity.BaseEntity;

/**
 * @author 谭永强
 * @date 2022-12-19
 */
public class SysProfile extends BaseEntity {

    /**
     * 主键
     */
    private Long profileId;
    /**
     * 配置名称
     */
    private String profileName;
    /**
     * 配置编码
     */
    private String profileCode;
    /**
     * 值来源类型(sql:SQL、fixed:固定值)
     */
    private String profileSourceType;
    /**
     * 值来源
     */
    private String profileSource;
    /**
     * 值列
     */
    private String profileValueColumn;
    /**
     * 编码列
     */
    private String profileCodeColumn;
    /**
     * 描述列
     */
    private String profileDescColumn;
    /**
     * 父级列
     */
    private String profileParentColumn;
    /**
     * 组件类型(popup:弹出框、input:输入框、select:选择框)
     */
    private String profileComponentType;
	/**
	 * 配置分类(来源于值集)
	 */
	private String profileType;
	/**
	 * 语言(zh-CN:中文、en-US:英文)，
	 * 取值：LanguageEnum
	 */
	private String language;
	/**
	 * 字段别名
	 * 当配置类型=数据权限时，才需要开放该字段，但是也是[可选]字段，多个别名可以使用逗号(,)分隔
	 */
	private String fieldAlias;

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfileCode() {
        return profileCode;
    }

    public void setProfileCode(String profileCode) {
        this.profileCode = profileCode;
    }

    public String getProfileSourceType() {
        return profileSourceType;
    }

    public void setProfileSourceType(String profileSourceType) {
        this.profileSourceType = profileSourceType;
    }

    public String getProfileSource() {
        return profileSource;
    }

    public void setProfileSource(String profileSource) {
        this.profileSource = profileSource;
    }

    public String getProfileValueColumn() {
        return profileValueColumn;
    }

    public void setProfileValueColumn(String profileValueColumn) {
        this.profileValueColumn = profileValueColumn;
    }

    public String getProfileCodeColumn() {
        return profileCodeColumn;
    }

    public void setProfileCodeColumn(String profileCodeColumn) {
        this.profileCodeColumn = profileCodeColumn;
    }

    public String getProfileDescColumn() {
        return profileDescColumn;
    }

    public void setProfileDescColumn(String profileDescColumn) {
        this.profileDescColumn = profileDescColumn;
    }

    public String getProfileParentColumn() {
        return profileParentColumn;
    }

    public void setProfileParentColumn(String profileParentColumn) {
        this.profileParentColumn = profileParentColumn;
    }

    public String getProfileComponentType() {
        return profileComponentType;
    }

    public void setProfileComponentType(String profileComponentType) {
        this.profileComponentType = profileComponentType;
    }

	public String getProfileType() {
		return profileType;
	}

	public void setProfileType(String profileType) {
		this.profileType = profileType;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getFieldAlias() {
		return fieldAlias;
	}

	public void setFieldAlias(String fieldAlias) {
		this.fieldAlias = fieldAlias;
	}
}