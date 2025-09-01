package com.crrc.sys.enums;

import java.util.Arrays;

/**
 * @ClassName: ValidInvalidStatusEnum
 * @Description: 人员状态枚举
 * @Author LiuQiang
 * @Date 2023/5/26
 */
public enum PersonStatusEnum {

    /**
     * 在职
     */
    Y("在职", "1"),
    /**
     * 离职
     */
    N("离职", "2"),
    /**
     * 样例
     */
    YN("在职/离职","YN");

    /**
     * 描述
     */
    private String text;
    /**
     * 值
     */
    private String value;

    PersonStatusEnum(String text, String value) {
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
    public static boolean contains(Integer value) {
        return Arrays.stream(PersonStatusEnum.values())
                .anyMatch(o -> o.getValue().equals(value));
    }
}
