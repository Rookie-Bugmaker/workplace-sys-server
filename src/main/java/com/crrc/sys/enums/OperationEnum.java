package com.crrc.sys.enums;

/**
 * @ClassName: OperationEnum
 * @Description: erp操作类型枚举
 * @Author LiuQiang
 * @Date 2023/10/19
 */
public enum OperationEnum {
    /**
     * 新增
     */
    ADD("新增", "A"),
    /**
     * 修改
     */
    UPDATE("修改", "M"),

    /**
     * 删除
     */
    REMOVE("删除","R"),

    /**
     * 查询
     */
    QUERY("查询", "Q");
    /**
     * 描述
     */
    private String text;
    /**
     * 值
     */
    private String value;

    OperationEnum(String text, String value) {
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
}
