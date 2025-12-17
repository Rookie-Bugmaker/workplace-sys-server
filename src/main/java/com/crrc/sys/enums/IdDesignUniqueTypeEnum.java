package com.crrc.sys.enums;

import java.util.Arrays;

/**
 * 自定义ID-唯一编码类型
 *
 * @author 李江涛
 * @date 2025-12-17
 */
public enum IdDesignUniqueTypeEnum {

    /**
     * 0:UUID
     */
    UUID("UUID", "0"),
    /**
     * 1:雪花ID
     */
    SNOWFLAKE("雪花ID", "1"),
    /**
     * 2:流水号
     */
    SERIAL("流水号", "2");

    /**
     * 描述
     */
    private String text;
    /**
     * 值
     */
    private String value;

    IdDesignUniqueTypeEnum(String text, String value) {
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
        return Arrays.stream(IdDesignUniqueTypeEnum.values())
                .anyMatch(o -> o.getValue().equals(value));
    }
}
