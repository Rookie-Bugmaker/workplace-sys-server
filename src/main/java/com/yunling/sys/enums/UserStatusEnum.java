package com.yunling.sys.enums;

/**
 * @ClassName: UserStatusEnum
 * @Description: 用户状态枚举类
 * @Author LiuQiang
 * @Date 2023/7/11
 */
public enum UserStatusEnum {

    /**
     *启用
     */
    N("N","启用"),
    /**
     * 禁用
     */
    Y("Y","禁用");

    /**
     * 描述
     */
    private String text;
    /**
     * 值
     */
    private String value;

    UserStatusEnum(String value, String text) {
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
