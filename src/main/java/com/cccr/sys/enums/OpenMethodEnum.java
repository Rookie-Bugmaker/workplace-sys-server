package com.cccr.sys.enums;

import java.util.Arrays;

/**
 * 配置-打开方式-枚举
 *
 * @author 谭永强
 * @date 2022-09-14
 */
public enum OpenMethodEnum {

    /**
     * 内部
     */
    INSIDE("内部", 0),
    /**
     * 外部
     */
    EXTERNAL("外部", 1);

    /**
     * 描述
     */
    private String text;
    /**
     * 值
     */
    private int value;

    OpenMethodEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    /**
     * 判断是否是枚举值
     *
     * @param value 值
     * @return true/false
     */
    public static boolean contains(Integer value) {
        return Arrays.stream(OpenMethodEnum.values())
                .anyMatch(o -> o.getValue().equals(value));
    }
}
