package com.crrc.sys.model.dept;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.crrc.sys.entity.excel.BaseExcelEntity;
import io.swagger.v3.oas.annotations.media.Schema;

public class DeptVerifyExcelDTO extends BaseExcelEntity {

    @ExcelProperty(value = {"*部门编码"}, index = 0)
    @Schema(description = "部门编码", example = "2")
    private String deptCode;

    @ExcelProperty(value = {"*部门名称"}, index = 1)
    @Schema(description = "部门名称", example = "流程与信息化")
    private String departmentName;

    @ExcelProperty(value = {"*所属公司编码"}, index = 2)
    @Schema(description = "所属公司编码", example = "1")
    private String companyCode;

    @ExcelProperty(value = {"*部门状态(正常/停用)"}, index = 3)
    @Schema(description = "部门状态", example = "0（可用）")
    private String status;

    @ExcelProperty(value = {"父级部门编码(不填写则为顶级部门)"}, index = 4)
    @Schema(description = "父级部门编码", example = "2")
    private String parentDeptCode;

    @ExcelProperty(value = {"部门排序"}, index = 5)
    @Schema(description = "部门排序", example = "0")
    private String departmentOrder;

    @ExcelProperty(value = {"备注"}, index = 6)
    @Schema(description = "备注", example = "备注")
    private String remark;

    @ExcelProperty(value = {"验证消息"}, index = 7)
    private String validateMsg;

    @ExcelProperty(value = {"验证状态"}, index = 8)
    private String validateStatus;

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

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getParentDeptCode() {
        return parentDeptCode;
    }

    public void setParentDeptCode(String parentDeptCode) {
        this.parentDeptCode = parentDeptCode;
    }

    public String getDepartmentOrder() {
        return departmentOrder;
    }

    public void setDepartmentOrder(String departmentOrder) {
        this.departmentOrder = departmentOrder;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
