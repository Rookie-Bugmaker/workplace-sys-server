package com.crrc.sys.model.dict;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 字典项
 * </p>
 *
 * @author lijiangtao
 * @since 2025-01-10
 */
public class DictItemExcelVO {
    @ExcelProperty(value = {"字典编码"}, index = 0)
    private String dictCode;

    @Schema(description = "字典项编码")
    @ExcelProperty(value = {"字典项编码"}, index = 1)
    private String itemCode;

    @Schema(description = "字典项名称")
    @ExcelProperty(value = {"字典项名称"}, index = 2)
    private String itemName;

    @Schema(description = "序列")
    @ExcelProperty(value = {"序列"}, index = 3)
    private String sorted;

    /**
     * lombok和easyexcel不兼容(或使用非链式)
     * @return
     */
    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
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

    public String getSorted() {
        return sorted;
    }

    public void setSorted(String sorted) {
        this.sorted = sorted;
    }
}
