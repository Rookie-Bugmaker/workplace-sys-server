package com.crrc.sys.entity.role;

/**
 * 角色信息分页查询参数
 *
 * @author 谭永强
 * @date 2022-09-07
 */
public class SysRoleFindByPageParam {

    /**
     * 当前页码
     */
    private Integer page;
    /**
     * 偏移量，当pageSize=0时查询所有数据
     */
    private Integer pageSize;
    /**
     * 语言，取值：Language枚举
     * ZHS中文、US英文
     */
    private String language;
    /**
     * 角色编码(模糊搜索)
     */
    private String roleCode;
    /**
     * 角色名称(模糊搜索)
     */
    private String roleName;
    /**
     * 角色描述(模糊搜索)
     */
    private String roleDescription;
    /**
     * 状态(Y/N:有效/无效)
     */
    private String enableFlag;
    /**
     * 菜单ID(搜索条件)
     */
    private Long menuId;
    /**
     * 项目ID
     */
    private Long projectId;

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

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
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

    public String getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
