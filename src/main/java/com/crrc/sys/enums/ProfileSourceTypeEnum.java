package com.crrc.sys.enums;

import java.util.Arrays;

/**
 * 值来源类型
 *
 * @author 谭永强
 * @date 2022-12-20
 */
public enum ProfileSourceTypeEnum {

    /**
     * sql:SQL
     */
    SQL("SQL", "sql"),
    /**
     * fixed:固定值
     */
    FIXED("固定值", "fixed");

    /**
     * 描述
     */
    private String text;
    /**
     * 值
     */
    private String value;

    ProfileSourceTypeEnum(String text, String value) {
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
        return Arrays.stream(ProfileSourceTypeEnum.values())
                .anyMatch(o -> o.getValue().equals(value));
    }
}
