package com.crrc.sys.enums.iam;

import com.crrc.sys.enums.DateFormatEnum;

/**
 * IDM-下推状态枚举
 * 定义IDM系统下推操作的各种状态类型
 *
 * @author ljt
 * @date 2025-01-21
 */
public enum IDMPushTypeEnum {
    /**
     * 新增操作
     */
    ADD(0, "新增"),

    /**
     * 修改操作
     */
    MODIFY(1, "修改"),

    /**
     * 删除操作
     */
    DELETE(2, "删除"),

    /**
     * 启用操作
     */
    ENABLE(3, "启用"),

    /**
     * 停用操作
     */
    DISABLE(4, "停用"),

    /**
     * 密码修改操作
     */
    PASSWORD_CHANGE(5, "密码修改"),

    /**
     * 组织合并操作
     */
    ORG_MERGE(6, "组织合并"),

    /**
     * 转岗操作
     */
    TRANSFER(7, "转岗"),

    /**
     * 离职操作
     */
    DIMISSION(8, "离职"),

    /**
     * 返聘操作
     */
    REHIRE(9, "返聘");

    /**
     * 操作描述
     */
    private String text;

    /**
     * 操作值
     */
    private Integer value;

    IDMPushTypeEnum(Integer value, String text) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public Integer getValue() {
        return value;
    }
}