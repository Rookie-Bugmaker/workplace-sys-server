package com.crrc.sys.enums;

/**
 * 系统模块枚举
 *
 * @author 谭永强
 * @date 2022-09-14
 */
public enum SysModuleEnum {

    /**
     * 系统设置
     */
    AUTH("认证中心", "/auth"),
    /**
     * 系统服务
     */
    SYS("系统服务", "/sys"),
    /**
     * 定时任务
     */
    SCHEDULE("定时任务", "/schedule"),
    /**
     * 消息推送
     */
    AUSTIN("消息推送", "/austin"),
    /**
     * 文件服务
     */
    FILE("文件服务", "/file"),
    /**
     * 低代码
     */
    DESIGN("低代码服务", "/lowcode"),
    /**
     * 工作流
     */
    WORKFLOW("工作流", "/workflow");


    /**
     * 描述
     */
    private String text;
    /**
     * 值
     */
    private String value;

    SysModuleEnum(String text, String value) {
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
