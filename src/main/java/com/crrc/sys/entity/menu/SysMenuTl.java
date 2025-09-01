package com.crrc.sys.entity.menu;

import com.crrc.sys.entity.BaseEntity;

/**
 * 菜单-多语言
 *
 * @author 谭永强
 * @date 2022-09-21
 */
public class SysMenuTl extends BaseEntity {

    /**
     * 菜单ID
     */
    private Long menuId;
    /**
     * 菜单名称
     */
    private String menuName;
    /**
     * 菜单描述
     */
    private String menuDescription;
    /**
     * 语言:LanguageEnum
     * zh-CN中文、en-US英文
     */
    private String language;

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
