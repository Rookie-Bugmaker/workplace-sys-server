package com.crrc.sys.entity.menu;

import com.crrc.sys.entity.BaseEntity;

/**
 * 菜单DTO
 *
 * @author 谭永强
 * @date 2023-11-28
 */
public class SysMenuDTO extends BaseEntity {
    /**
     * 菜单ID
     */
    private Long menuId;
    /**
     * 菜单编码
     */
    private String menuCode;
    /**
     * 菜单名称
     */
    private String menuName;
    /**
     * 菜单描述
     */
    private String menuDescription;
    /**
     * 排序号
     */
    private Integer menuSort;
    /**
     * 功能ID
     */
    private Long functionId;
    /**
     * 图标
     */
    private String menuIcon;
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

    public String getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getChildId() {
        return childId;
    }

    public void setChildId(Long childId) {
        this.childId = childId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
