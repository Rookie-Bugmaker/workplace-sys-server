package com.crrc.sys.model.dict;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "字典及字典项导入VO")
public class DictAndItemExcelVO {
    @Schema(description = "字典编码")
    @ExcelProperty(value = {"字典编码"}, index = 0)
    private String dictCode;

    @Schema(description = "字典名称")
    @ExcelProperty(value = {"字典名称"}, index = 1)
    private String dictName;

    @Schema(description = "字典类型 0：系统 1：业务")
    @ExcelProperty(value = {"字典类型 0：系统 1：业务"}, index = 2)
    private String dictType;

    @Schema(description = "字典说明")
    @ExcelProperty(value = {"字典说明"}, index = 3)
    private String dictRemark;

    @Schema(description = "字典项编码")
    @ExcelProperty(value = {"字典项编码"}, index = 4)
    private String itemCode;

    @Schema(description = "字典项名称")
    @ExcelProperty(value = {"字典项名称"}, index = 5)
    private String itemName;

    @Schema(description = "字典项排序")
    @ExcelProperty(value = {"字典项排序"}, index = 6)
    private String itemSorted;

    // Getters and Setters
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

    public String getDictRemark() {
        return dictRemark;
    }

    public void setDictRemark(String dictRemark) {
        this.dictRemark = dictRemark;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemSorted() {
        return itemSorted;
    }

    public void setItemSorted(String itemSorted) {
        this.itemSorted = itemSorted;
    }
}