package com.crrc.sys.entity.function.column;

import com.crrc.sys.entity.BaseEntityYunLing;

import java.util.List;

/**
 * 列权限
 *
 * @author 谭永强
 * @date 2022-10-31
 */
public class SysFunctionColumn extends BaseEntityYunLing {

    /**
     * 主键
     */
    private Long fcId;
    /**
     * 功能ID
     */
    private Long functionId;
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
	 * 列类型(varchar:字符、number:数字、date:日期)
	 */
	private String fcType;
	/**
	 * 默认显示(Y:是、N:否)
	 */
	private String fcDisplayFlag;
	/**
	 * 默认编辑(Y:是、N:否)
	 */
	private String fcEditableFlag;
    /**
     * 上级ID(目前只设计了2层，所以此处的parent_id等于块ID)
     */
    private Long parentId;
	/**
	 * 值集ID
	 */
	private Long vsId;
	/**
	 * 值集编码
	 */
	private String vsCode;
    /**
	 * 列权限集合
     */
    private List<SysFunctionColumn> children;
	/**
	 * 语言:LanguageEnum
	 * zh-CN:中文、en-US:英文
	 */
	private String language;
	/**
	 * 隐藏列(true/false)
	 */
	private Boolean hideColumn = true;
	/**
	 * 实际编辑(Y:是、N:否)
	 */
	private String editableFlag = "Y";

    public Long getFcId() {
        return fcId;
    }

    public void setFcId(Long fcId) {
        this.fcId = fcId;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public String getFcCode() {
        return fcCode;
    }

    public void setFcCode(String fcCode) {
        this.fcCode = fcCode;
    }

    public String getFcDesc() {
        return fcDesc;
    }

    public void setFcDesc(String fcDesc) {
        this.fcDesc = fcDesc;
    }

	public String getFcDisplayFlag() {
		return fcDisplayFlag;
	}

	public void setFcDisplayFlag(String fcDisplayFlag) {
		this.fcDisplayFlag = fcDisplayFlag;
	}

	public String getFcEditableFlag() {
		return fcEditableFlag;
	}

	public void setFcEditableFlag(String fcEditableFlag) {
		this.fcEditableFlag = fcEditableFlag;
	}

	public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<SysFunctionColumn> getChildren() {
        return children;
    }

    public void setChildren(List<SysFunctionColumn> children) {
        this.children = children;
    }

	public String getFcName() {
		return fcName;
	}

	public void setFcName(String fcName) {
		this.fcName = fcName;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Long getVsId() {
		return vsId;
	}

	public void setVsId(Long vsId) {
		this.vsId = vsId;
	}

	public Boolean getHideColumn() {
		return hideColumn;
	}

	public void setHideColumn(Boolean hideColumn) {
		this.hideColumn = hideColumn;
	}

	public String getVsCode() {
		return vsCode;
	}

	public void setVsCode(String vsCode) {
		this.vsCode = vsCode;
	}

	public String getEditableFlag() {
		return editableFlag;
	}

	public void setEditableFlag(String editableFlag) {
		this.editableFlag = editableFlag;
	}

	public String getFcType() {
		return fcType;
	}

	public void setFcType(String fcType) {
		this.fcType = fcType;
	}
}
