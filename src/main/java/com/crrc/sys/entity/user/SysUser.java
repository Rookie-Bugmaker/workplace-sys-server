package com.crrc.sys.entity.user;

import com.crrc.sys.entity.BaseEntityYunLing;
import com.crrc.sys.entity.auth.SysDataAuth;
import com.crrc.sys.entity.role.SysRole;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 用户信息
 *
 * @author 谭永强
 * @date 2022-09-02
 */
public class SysUser extends BaseEntityYunLing {

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户描述
     */
    private String userDescription;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 密码修改时间
     */
    private Date passwordUpdateDate;
    /**
     * 密码失效时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date passwordExpireDate;
    /**
     * 用户类型
     * EMPLOYEE：员工、SUPPLIER：供应商、CUSTOMER：客户
     */
    private String userType;
    /**
     * 用户关联ID
     */
    private Long userRelationId;
    /**
     * 密码错误次数
     */
    private Integer loginErrorCount;
    /**
     * 最后登录信息ID
     */
    private Long lastLoginInfoId;
    /**
     * 禁用截止日期
     */
    private Date disableDeadlineDate;
    /**
     * 禁用状态(Y/N)
     */
    private String disableState;
    /**
     * 下一次登录修改密码(Y:是、N:否)
     */
    private String nextLoginUpdatePassword = "N";
    /**
     * 拥有的角色信息
     */
    private List<SysRole> roleList;
    /**
     * 拥有的角色ID
     */
    private List<Long> roleIdList;
    /**
     * 用户数据权限
     */
    private List<SysDataAuth> dataAuthList;
    /**
     * 用户角色列表
     * updateBy LiuQiang
     */
    private List<SysUserRole> userRoleList;
    /**
     * 当前角色ID
     */
    private Long currentRoleId;
    /**
     * 授予的角色ID(前端传递参数使用)
     */
    private List<Long> roleIds;
    /**
     * 当前公司ID
     */
    private Long currentCompanyId;
    /**
     * 当前部门ID
     */
    private Long currentDeptId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getPasswordUpdateDate() {
        return passwordUpdateDate;
    }

    public void setPasswordUpdateDate(Date passwordUpdateDate) {
        this.passwordUpdateDate = passwordUpdateDate;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Long getUserRelationId() {
        return userRelationId;
    }

    public void setUserRelationId(Long userRelationId) {
        this.userRelationId = userRelationId;
    }

    public Integer getLoginErrorCount() {
        return loginErrorCount;
    }

    public void setLoginErrorCount(Integer loginErrorCount) {
        this.loginErrorCount = loginErrorCount;
    }

    public Long getLastLoginInfoId() {
        return lastLoginInfoId;
    }

    public void setLastLoginInfoId(Long lastLoginInfoId) {
        this.lastLoginInfoId = lastLoginInfoId;
    }

    public Date getDisableDeadlineDate() {
        return disableDeadlineDate;
    }

    public void setDisableDeadlineDate(Date disableDeadlineDate) {
        this.disableDeadlineDate = disableDeadlineDate;
    }

    public String getDisableState() {
        return disableState;
    }

    public void setDisableState(String disableState) {
        this.disableState = disableState;
    }

    public List<SysRole> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<SysRole> roleList) {
        this.roleList = roleList;
    }

    public List<Long> getRoleIdList() {
        return roleIdList;
    }

    public void setRoleIdList(List<Long> roleIdList) {
        this.roleIdList = roleIdList;
    }

    public List<SysDataAuth> getDataAuthList() {
        return dataAuthList;
    }

    public void setDataAuthList(List<SysDataAuth> dataAuthList) {
        this.dataAuthList = dataAuthList;
    }

    public List<SysUserRole> getUserRoleList() {
        return userRoleList;
    }

    public void setUserRoleList(List<SysUserRole> userRoleList) {
        this.userRoleList = userRoleList;
    }

    public Long getCurrentRoleId() {
        return currentRoleId;
    }

    public void setCurrentRoleId(Long currentRoleId) {
        this.currentRoleId = currentRoleId;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    public Long getCurrentCompanyId() {
        return currentCompanyId;
    }

    public void setCurrentCompanyId(Long currentCompanyId) {
        this.currentCompanyId = currentCompanyId;
    }

    public Long getCurrentDeptId() {
        return currentDeptId;
    }

    public void setCurrentDeptId(Long currentDeptId) {
        this.currentDeptId = currentDeptId;
    }

    public Date getPasswordExpireDate() {
        return passwordExpireDate;
    }

    public void setPasswordExpireDate(Date passwordExpireDate) {
        this.passwordExpireDate = passwordExpireDate;
    }

    public String getNextLoginUpdatePassword() {
        return nextLoginUpdatePassword;
    }

    public void setNextLoginUpdatePassword(String nextLoginUpdatePassword) {
        this.nextLoginUpdatePassword = nextLoginUpdatePassword;
    }
}
