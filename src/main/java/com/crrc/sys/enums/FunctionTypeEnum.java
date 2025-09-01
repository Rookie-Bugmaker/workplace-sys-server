package com.crrc.sys.enums;

import java.util.Arrays;

/**
 * 功能类型枚举
 *
 * @author 谭永强
 * @date 2022-09-14
 */
public enum FunctionTypeEnum {

    /**
     * A:功能
     */
    A("功能", "A"),
    /**
     * B:按钮
     */
    B("按钮", "B");

    /**
     * 描述
     */
    private String text;
    /**
     * 值
     */
    private String value;

    FunctionTypeEnum(String text, String value) {
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
        return Arrays.stream(FunctionTypeEnum.values())
                .anyMatch(o -> o.getValue().equals(value));
    }
}
