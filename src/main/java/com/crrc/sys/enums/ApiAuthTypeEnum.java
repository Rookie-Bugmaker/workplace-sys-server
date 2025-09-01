package com.crrc.sys.enums;

import java.util.Arrays;

/**
 * 权限类型分类
 *
 * @author 谭永强
 * @date 2022-09-14
 */
public enum ApiAuthTypeEnum {

    /**
     * 白名单
     */
    A("白名单", "A"),
    /**
     * 黑名单
     */
    B("黑名单", "B");


    /**
     * 描述
     */
    private String text;
    /**
     * 值
     */
    private String value;

    ApiAuthTypeEnum(String text, String value) {
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
        return Arrays.stream(ApiAuthTypeEnum.values())
                .anyMatch(o -> o.getValue().equals(value));
    }
}
