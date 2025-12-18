package com.crrc.sys.model.user.role;

import com.alibaba.excel.annotation.ExcelProperty;
import com.crrc.sys.entity.excel.BaseExcelEntity;

import java.util.Date;

/**
 * 角色人员-excel导入\导出VO
 */
public class SysUserRoleDeliveryExcelVO extends BaseExcelEntity {
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
}
