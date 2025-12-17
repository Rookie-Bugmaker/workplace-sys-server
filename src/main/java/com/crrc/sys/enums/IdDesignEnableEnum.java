package com.crrc.sys.enums;

import java.util.Arrays;

/**
 * 自定义ID-启用状态
 *
 * @author 李江涛
 * @date 2025-12-17
 */
public enum IdDesignEnableEnum {

    /**
     * 0:启用
     */
    TRUE("启用", "0"),
    /**
     * 1:不启用
     */
    FALSE("不启用", "1");

    /**
     * 描述
     */
    private String text;
    /**
     * 值
     */
    private String value;

    IdDesignEnableEnum(String text, String value) {
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
        return Arrays.stream(IdDesignEnableEnum.values())
                .anyMatch(o -> o.getValue().equals(value));
    }
}
