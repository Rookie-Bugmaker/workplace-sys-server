package com.cccr.sys.entity.menu;

import com.cccr.sys.entity.function.SysFunction;
import com.cccr.sys.entity.BaseEntity;

import java.util.List;

/**
 * 菜单实体
 *
 * @author 谭永强
 * @date 2022-09-19
 */
public class SysMenu extends BaseEntity {

    /**
     * 菜单ID
     */
    private Long menuId;
    /**
     * 菜单编码
     */
    private String menuCode;
    /**
     * 排序号
     */
    private Integer menuSort;
    /**
     * 功能ID
     */
    private Long functionId;
    /**
     * 父级菜单ID
     */
    private Long parentId;
    /**
     * 子菜单ID
     */
    private Long childId;
    /**
     * 语言:LanguageEnum
     * zh-CN:中文、en-US:英文
     */
    private String language;
    /**
     * 菜单名称
     */
    private String menuName;
    /**
     * 菜单描述
     */
    private String menuDescription;
    /**
     * 图标
     */
    private String menuIcon;
    /**
     * 功能信息
     */
    private SysFunction function;
    /**
     * 子菜单集合
     */
    private List<SysMenu> children;
	/**
	 * 子菜单编码
	 */
	private String childCode;
    /**
     * 子菜单名称
     */
    private String childName;

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public Integer getMenuSort() {
        return menuSort;
    }

    public void setMenuSort(Integer menuSort) {
        this.menuSort = menuSort;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuDescription() {
        return menuDescription;
    }

    public void setMenuDescription(String menuDescription) {
        this.menuDescription = menuDescription;
    }

    public SysFunction getFunction() {
        return function;
    }

    public void setFunction(SysFunction function) {
        this.function = function;
    }

    public List<SysMenu> getChildren() {
        return children;
    }

    public void setChildren(List<SysMenu> children) {
        this.children = children;
    }

    public String getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    public Long getChildId() {
        return childId;
    }

    public void setChildId(Long childId) {
        this.childId = childId;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

	public String getChildCode() {
		return childCode;
	}

	public void setChildCode(String childCode) {
		this.childCode = childCode;
	}
}
