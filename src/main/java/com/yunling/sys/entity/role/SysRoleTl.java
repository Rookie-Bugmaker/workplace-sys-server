package com.yunling.sys.entity.role;

import com.yunling.sys.entity.BaseEntity;

/**
 * 角色-国际化
 *
 * @author 谭永强
 * @date 2022-09-09
 */
public class SysRoleTl extends BaseEntity {

    /**
     * 角色ID
     */
    private Long roleId;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 描述
     */
    private String roleDescription;
    /**
     * 语言:LanguageEnum
     * zh-CN中文、en-US英文
     */
    private String language;

    public SysRoleTl() {
    }

    public SysRoleTl(Long roleId, String roleName, String roleDescription, String language) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleDescription = roleDescription;
        this.language = language;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
