package com.crrc.sys.model.user.role;

import com.alibaba.excel.annotation.ExcelProperty;
import com.crrc.sys.entity.excel.BaseExcelEntity;

/**
 * 角色人员-excel验证信息导出DTO
 */
public class SysUserRoleVerifyExcelDTO extends BaseExcelEntity {

    /**
     * 角色编码
     */
    @ExcelProperty(value = {"*角色编码"}, index = 0)
    private String roleCode;

    /**
     * 用户名
     */
    @ExcelProperty(value = {"*用户名"}, index = 1)
    private String userName;

    @ExcelProperty(value = {"验证消息"}, index = 2)
    private String validateMsg;

    @ExcelProperty(value = {"验证状态"}, index = 3)
    private String validateStatus;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
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
}
