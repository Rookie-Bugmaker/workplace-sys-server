package com.yunling.sys.enums;

import java.util.Arrays;

/**
 * 性别枚举
 *
 * @author 谭永强
 * @date 2022-12-09
 */
public enum GenderEnum {

    /**
     * M:男
     */
    M("男", "M"),
    /**
     * F：女
     */
    F("女", "F");

    /**
     * 描述
     */
    private String text;
    /**
     * 值
     */
    private String value;

    GenderEnum(String text, String value) {
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

    /**
     * 判断是否是枚举值
     *
     * @param value 值
     * @return true/false
     */
    public static boolean contains(String value) {
        return Arrays.stream(GenderEnum.values())
                .anyMatch(o -> o.getValue().equals(value));
    }
}
