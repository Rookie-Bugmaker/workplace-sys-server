package com.crrc.sys.enums;

/**
 * 操作类型
 *
 * @author 谭永强
 * @date 2022-10-11
 */
public enum OperationTypeEnum {

    /**
     * Y:有效
     */
    ADD("新增", "add"),
    /**
     * N：无效
     */
    UPDATE("修改", "update");

    /**
     * 描述
     */
    private String text;
    /**
     * 值
     */
    private String value;

    OperationTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
