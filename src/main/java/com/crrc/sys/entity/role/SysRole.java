package com.crrc.sys.entity.role;

import com.crrc.sys.entity.BaseEntityYunLing;
import com.crrc.sys.entity.auth.SysDataAuth;
import com.crrc.sys.model.role.SysRoleCompanyDeptDTO;

import java.util.List;

/**
 * 角色实体
 *
 * @author 谭永强
 * @date 2022-09-07
 */
public class SysRole extends BaseEntityYunLing {

    /**
     * 角色ID
     */
    private Long roleId;
    /**
     * 角色编码
     */
    private String roleCode;
    /**
     * 有效状态(Y/N)
     */
    private String enableFlag;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 描述
     */
    private String roleDescription;
    /**
     * 菜单ID
     */
    private Long menuId;
    /**
     * 语言:LanguageEnum
     * zh-CN中文、en-US英文
     */
    private String language;
    /**
     * 角色菜单
     */
    private List<SysRoleMenu> roleMenuList;
    /**
     * 选中的菜单ID
     */
    private List<Long> checkedMenuIds;
    /**
     * 半选中的菜单ID
     */
    private List<Long> halfCheckedMenuIds;
    /**
     * 角色列权限值集
     */
    private List<SysRoleColumnValueSet> roleColumnValueSets;
    /**
     * 选中的角色列权限
     */
    private List<SysRoleFunctionColumn> checkedFunctionColumns;
    /**
     * 角色数据权限
     */
    private List<SysDataAuth> dataAuthList;
    /**
     * 所属项目
     */
    private Long projectId;
    /**
     * 所属项目名称
     */
    private String projectName;
    /**
     * 公司部门集合
     */
    private List<SysRoleCompanyDeptDTO> roleCompanyDeptList;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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

    public String getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<SysRoleMenu> getRoleMenuList() {
        return roleMenuList;
    }

    public void setRoleMenuList(List<SysRoleMenu> roleMenuList) {
        this.roleMenuList = roleMenuList;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public List<SysRoleColumnValueSet> getRoleColumnValueSets() {
        return roleColumnValueSets;
    }

    public void setRoleColumnValueSets(List<SysRoleColumnValueSet> roleColumnValueSets) {
        this.roleColumnValueSets = roleColumnValueSets;
    }

    public List<SysRoleFunctionColumn> getCheckedFunctionColumns() {
        return checkedFunctionColumns;
    }

    public void setCheckedFunctionColumns(List<SysRoleFunctionColumn> checkedFunctionColumns) {
        this.checkedFunctionColumns = checkedFunctionColumns;
    }

    public List<SysDataAuth> getDataAuthList() {
        return dataAuthList;
    }

    public void setDataAuthList(List<SysDataAuth> dataAuthList) {
        this.dataAuthList = dataAuthList;
    }

    public List<Long> getCheckedMenuIds() {
        return checkedMenuIds;
    }

    public void setCheckedMenuIds(List<Long> checkedMenuIds) {
        this.checkedMenuIds = checkedMenuIds;
    }

    public List<Long> getHalfCheckedMenuIds() {
        return halfCheckedMenuIds;
    }

    public void setHalfCheckedMenuIds(List<Long> halfCheckedMenuIds) {
        this.halfCheckedMenuIds = halfCheckedMenuIds;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<SysRoleCompanyDeptDTO> getRoleCompanyDeptList() {
        return roleCompanyDeptList;
    }

    public void setRoleCompanyDeptList(List<SysRoleCompanyDeptDTO> roleCompanyDeptList) {
        this.roleCompanyDeptList = roleCompanyDeptList;
    }
}
