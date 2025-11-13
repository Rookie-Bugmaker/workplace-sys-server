package com.crrc.sys.model.company;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.crrc.sys.entity.excel.BaseExcelEntity;
import io.swagger.v3.oas.annotations.media.Schema;

public class CompanyVerifyExcelDTO extends BaseExcelEntity {
    @Schema(description = "公司id", example = "2000")
    @ExcelProperty(value = {"*公司ID"}, index = 0)
    private String companyId;

    @Schema(description = "组织编码", example = "2")
    @ExcelProperty(value = {"*组织编码"}, index = 1)
    private String orgCode;

    @Schema(description = "公司名称", example = "中车长客")
    @ExcelProperty(value = {"*公司名称"}, index = 2)
    private String companyName;

    @Schema(description = "公司状态", example = "0（可用）")
    @ExcelProperty(value = {"*公司状态(正常/停用)"}, index = 3)
    private String status;

    @Schema(description = "工厂编码", example = "2000")
    @ExcelProperty(value = {"工厂编码"}, index = 4)
    private String plantCode;


    @Schema(description = "备注", example = "备注")
    @ExcelProperty(value = {"备注"}, index = 5)
    private String remark;

    @ExcelProperty(value = {"验证消息"}, index = 6)
    private String validateMsg;

    @ExcelProperty(value = {"验证状态"}, index = 7)
    private String validateStatus;



    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlantCode() {
        return plantCode;
    }

    public void setPlantCode(String plantCode) {
        this.plantCode = plantCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
