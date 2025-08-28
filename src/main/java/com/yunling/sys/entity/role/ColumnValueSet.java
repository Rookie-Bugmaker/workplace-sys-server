package com.yunling.sys.entity.role;

/**
 * 列权限-值集
 *
 * @author 谭永强
 * @date 2022-11-09
 */
public class ColumnValueSet {

    /**
     * 列编码
     */
    private String columnCode;
    /**
     * 值集编码
     */
    private String vsCode;

    public String getColumnCode() {
        return columnCode;
    }

    public void setColumnCode(String columnCode) {
        this.columnCode = columnCode;
    }

    public String getVsCode() {
        return vsCode;
    }

    public void setVsCode(String vsCode) {
        this.vsCode = vsCode;
    }
}
