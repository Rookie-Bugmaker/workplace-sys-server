package com.cccr.sys.enums;

import java.util.Arrays;

/**
 * 有效状态
 *
 * @author 谭永强
 * @date 2022-09-09
 */
public enum EnableFlagEnum {

    /**
     * Y:有效
     */
    Y("有效", "Y"),
    /**
     * N：无效
     */
    N("无效", "N");

    /**
     * 描述
     */
    private String text;
    /**
     * 值
     */
    private String value;

    EnableFlagEnum(String text, String value) {
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
        return Arrays.stream(EnableFlagEnum.values())
                .anyMatch(o -> o.getValue().equals(value));
    }
}
