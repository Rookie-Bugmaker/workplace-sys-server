package com.crrc.sys.entity.user;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.nacos.shaded.com.google.common.base.Objects;
import com.crrc.sys.entity.excel.BaseExcelEntity;

import java.util.Date;

/**
 * @ClassName: UserExcelEntity
 * @Description: 用户Excel操作实体
 * @Author LiuQiang
 * @Date 2023/7/11
 */
public class UserExcelEntity extends BaseExcelEntity {

    /**
     * 用户名
     */
    @ExcelProperty(value = "*用户名", index = 0)
    private String userName;

    /**
     * 用户密码
     */
    @ExcelProperty(value = "*用户密码", index = 1)
    private String password;
    /**
     * 用户类型
     * EMPLOYEE：员工、SUPPLIER：供应商、CUSTOMER：客户
     */
    @ExcelProperty(value = "*用户类型", index = 2)
    private String userType;
    /**
     * 用户关联
     */
    @ExcelProperty(value = "关联用户", index = 3)
    private String userRelation;
    /**
     * 登录终端
     */
//    @ExcelProperty(value = "*登录终端")
//    private String terminals;

    /**
     * 账户生效时间
     */
//    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
//    @ExcelProperty(value = "*生效时间")
//    private Date startDate;
    /**
     * 账户失效时间
     */
//    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
//    @ExcelProperty(value = "失效时间")
//    private Date endDate;
    /**
     * 禁用状态(Y/N)
     */
    @ExcelProperty(value = "*用户状态", index = 4)
    private String disableState;
    /**
     * 用户描述
     */
    @ExcelProperty(value = "*用户描述", index = 5)
    private String userDescription;

    /**
     * 角色授权
     */
    @ExcelProperty(value = "*角色授权", index = 6)
    private String roleAuth;


    @ExcelProperty(value = "验证消息")
    private String validateMsg;
    @ExcelProperty(value = "验证状态")
    private String validateStatus;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserRelation() {
        return userRelation;
    }

    public void setUserRelation(String userRelation) {
        this.userRelation = userRelation;
    }

//    public String getTerminals() {
//        return terminals;
//    }
//
//    public void setTerminals(String terminals) {
//        this.terminals = terminals;
//    }

//    public Date getStartDate() {
//        return startDate;
//    }
//
//    public void setStartDate(Date startDate) {
//        this.startDate = startDate;
//    }
//
//    public Date getEndDate() {
//        return endDate;
//    }
//
//    public void setEndDate(Date endDate) {
//        this.endDate = endDate;
//    }

    public String getDisableState() {
        return disableState;
    }

    public void setDisableState(String disableState) {
        this.disableState = disableState;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public String getRoleAuth() {
        return roleAuth;
    }

    public void setRoleAuth(String roleAuth) {
        this.roleAuth = roleAuth;
    }

    public String getValidateMsg() {
        return validateMsg;
    }

    public void setValidateMsg(String validateMsg) {
        this.validateMsg = validateMsg;
    }

    public String getValidateStatus() {
        return validateStatus;
    }

    public void setValidateStatus(String validateStatus) {
        this.validateStatus = validateStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserExcelEntity that = (UserExcelEntity) o;
        return Objects.equal(userName, that.userName) && Objects.equal(password, that.password) && Objects.equal(userType, that.userType) && Objects.equal(userRelation, that.userRelation)  && Objects.equal(disableState, that.disableState) && Objects.equal(userDescription, that.userDescription) && Objects.equal(roleAuth, that.roleAuth);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userName, password, userType, userRelation, disableState, userDescription, roleAuth);
    }
}
