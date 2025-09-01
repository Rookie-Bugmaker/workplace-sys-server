package com.cccr.sys.entity.user;

import java.util.Date;

/**
 * 用户登录信息
 *
 * @author 谭永强
 * @date 2022-09-07
 */
public class SysUserLogin {

    /**
     * 登录ID
     */
    private Long loginId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 人员ID
     */
    private Long personId;
    /**
     * 登录时间
     */
    private Date loginDate;
    /**
     * 客户端类型
     * WEB = WEB端
     * PDA/APP = 移动端
     * CLIENT = 客户端
     * APPLICATION = 应用端
     */
    private String loginType;
    /**
     * 客户端信息
     */
    private String clientInfo;
    /**
     * 登录IP
     */
    private String loginIp;
    /**
     * 登录物理地址
     */
    private String loginMacAddress;
    /**
     * 登录状态
     */
    private Boolean loginStatus;
    /**
     * 登录描述
     */
    private String loginDesc;
    /**
     * 用户名(前端展示使用)
     */
    private String userName;
    /**
     * 用户姓名(前端展示使用)
     */
    private String fullName;
    /**
     * 工号(前端展示使用)
     */
    private String jobNo;
    /**
     * 部门(前端展示使用)
     */
    private String deptName;

    public Long getLoginId() {
        return loginId;
    }

    public void setLoginId(Long loginId) {
        this.loginId = loginId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getLoginMacAddress() {
        return loginMacAddress;
    }

    public void setLoginMacAddress(String loginMacAddress) {
        this.loginMacAddress = loginMacAddress;
    }

    public Boolean getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(Boolean loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getLoginDesc() {
        return loginDesc;
    }

    public void setLoginDesc(String loginDesc) {
        this.loginDesc = loginDesc;
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getJobNo() {
        return jobNo;
    }

    public void setJobNo(String jobNo) {
        this.jobNo = jobNo;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }
}
