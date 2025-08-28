package com.yunling.sys.entity.profile;

/**
 * @author 谭永强
 * @date 2022-12-19
 */
public class SysProfileFindByPageParam {

    /**
     * 当前页码
     */
    private Integer page;
    /**
     * 偏移量，当pageSize=0时查询所有数据
     */
    private Integer pageSize;
    /**
     * 语言(zh-CN:中文、en-US:英文)，
     * 取值：LanguageEnum
     */
    private String language;
    /**
     * 配置名称
     */
    private String profileName;
    /**
     * 配置编码
     */
    private String profileCode;
	/**
	 * 配置分类(来源于值集)
	 */
	private String profileType;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
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

    public String getProfileCode() {
        return profileCode;
    }

    public void setProfileCode(String profileCode) {
        this.profileCode = profileCode;
    }

	public String getProfileType() {
		return profileType;
	}

	public void setProfileType(String profileType) {
		this.profileType = profileType;
	}
}
