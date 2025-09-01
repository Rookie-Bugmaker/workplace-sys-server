package com.crrc.sys.enums;

/**
 * @ClassName: UserTypeEnum
 * @Description: 用户类型枚举类
 * @Author LiuQiang
 * @Date 2023/7/11
 */
public enum UserTypeEnum {

    /**
     * 员工
     */
    EMPLOYEE("EMPLOYEE","员工"),
    /**
     * 供应商
     */
    SUPPLIER("SUPPLIER","供应商"),
    /**
     * 客户
     */
    CUSTOMER("CUSTOMER","客户");

    /**
     * 描述
     */
    private String text;
    /**
     * 值
     */
    private String value;

    UserTypeEnum(String value, String text) {
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
