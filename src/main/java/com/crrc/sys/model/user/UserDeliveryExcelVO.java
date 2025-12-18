package com.crrc.sys.model.user;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.nacos.shaded.com.google.common.base.Objects;
import com.crrc.sys.entity.excel.BaseExcelEntity;

/**
 * @ClassName: UserDeliveryExcelVO
 * @Description: excel导入VO
 * @Author lijaingtao
 * @Date 2025/12/18
 */
public class UserDeliveryExcelVO extends BaseExcelEntity {

    /**
     * 用户名
     */
    @ExcelProperty(value = "*用户名", index = 0)
    private String userName;

    /**
     * 用户类型
     * EMPLOYEE：员工、SUPPLIER：供应商、CUSTOMER：客户
     */
    @ExcelProperty(value = "*用户类型", index = 1)
    private String userType;
    /**
     * 用户关联
     */
    @ExcelProperty(value = "关联用户", index = 2)
    private String userRelation;
    /**
     * 禁用状态(Y/N)
     */
    @ExcelProperty(value = "*用户状态", index = 3)
    private String disableState;
    /**
     * 用户描述
     */
    @ExcelProperty(value = "*用户描述", index = 4)
    private String userDescription;

    /**
     * 角色授权
     */
    @ExcelProperty(value = "角色授权", index = 5)
    private String roleAuth;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDeliveryExcelVO that = (UserDeliveryExcelVO) o;
        return Objects.equal(userName, that.userName) && Objects.equal(userType, that.userType) && Objects.equal(userRelation, that.userRelation)  && Objects.equal(disableState, that.disableState) && Objects.equal(userDescription, that.userDescription) && Objects.equal(roleAuth, that.roleAuth);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userName, userType, userRelation, disableState, userDescription, roleAuth);
    }
}
