package com.crrc.sys.enums;

/**
 * @ClassName: PersonTypeEnum
 * @Description: 人员类型枚举类
 * @Author lijiangtao
 * @Date 2025/11/12
 */
public enum PersonTypeEnum {

    /**
     * 员工
     */
    EMPLOYEE("1","员工"),
    /**
     * 供应商
     */
    SUPPLIER("2","供应商");

    /**
     * 描述
     */
    private String text;
    /**
     * 值
     */
    private String value;

    PersonTypeEnum(String value, String text) {
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
