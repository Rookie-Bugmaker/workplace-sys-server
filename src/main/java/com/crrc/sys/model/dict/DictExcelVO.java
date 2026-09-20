package com.crrc.sys.model.dict;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "字典VO")
public class DictExcelVO {
    @Schema(description = "字典编码")
    @ExcelProperty(value = {"字典编码"}, index = 0)
    private String dictCode;

    @Schema(description = "字典名称")
    @ExcelProperty(value = {"字典名称"}, index = 1)
    private String dictName;

    @Schema(description = "字典类型 0：系统 1：业务")
    @ExcelProperty(value = {"字典类型 0：系统 1：业务"}, index = 2)
    private String dictType;

    @Schema(description = "说明", example = "demo")
    @ExcelProperty(value = {"说明"}, index = 3)
    private String remark;

    @Schema(description = "项目编码（空为公共字典）")
    @ExcelProperty(value = {"项目编码"}, index = 4)
    private String projectCode;

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getDictType() {
        return dictType;
    }

    public void setDictType(String dictType) {
        this.dictType = dictType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }
}
