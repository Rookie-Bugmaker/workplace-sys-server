package com.crrc.sys.enums;

/**
 * @ClassName: DeptStatusEnum
 * @Description: 部门状态枚举类
 * @Author lijiangtao
 * @Date 2025/11/12
 */
public enum DeptStatusEnum {

    /**
     *正常
     */
    NORMAL("0","正常"),
    /**
     * 停用
     */
    DISABLE ("1","停用");

    /**
     * 描述
     */
    private String text;
    /**
     * 值
     */
    private String value;

    DeptStatusEnum(String value, String text) {
        this.value = value;
        this.text = text;
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
