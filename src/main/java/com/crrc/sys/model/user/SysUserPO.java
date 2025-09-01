package com.crrc.sys.model.user;

import com.crrc.sys.entity.BaseEntityYunLing;

import java.util.Date;

/**
 * 用户持久化对象
 *
 * @author 谭永强
 * @date 2024-01-30
 */
public class SysUserPO extends BaseEntityYunLing {

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
     * 密码
     */
    private String password;
    /**
     * 修改密码时间
     */
    private Date passwordUpdateDate;
    /**
     * 账户生效时间
     */
    private Date startDate;
    /**
     * 账户失效时间
     */
    private Date endDate;
    /**
     * 用户类型 EMPLOYEE员工 SUPPLIER供应商 CUSTOMER客户
     */
    private String userType;
    /**
     * 用户关联ID
     */
    private Long userRelationId;
    /**
     * 登录错误次数
     */
    private Long loginErrorCount;
    /**
     * 禁用截止时间
     */
    private Date disableDeadlineDate;
    /**
     * 禁用状态(Y/N)
     */
    private String disableState;

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public Long getLoginErrorCount() {
        return loginErrorCount;
    }

    public void setLoginErrorCount(Long loginErrorCount) {
        this.loginErrorCount = loginErrorCount;
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
}