package com.crrc.sys.enums;


public enum LogType {
    TYPE_UNKNOW(0, "未知"),

    TYPE_LOGIN(1, "登录"),
    TYPE_OPERATE(2, "操作"),

    TYPE_CRONJOB(3, "定时任务");

    private int logType;

    private String description;

    public int getLogType() {
        return this.logType;
    }

    public String getDescription() {
        return this.description;
    }

    LogType(int logType, String description) {
        this.logType = logType;
        this.description = description;
    }

    public static LogType fromDescription(String description) {
        switch (description) {
            case "登录":
                return LogType.TYPE_LOGIN;
            case "操作":
                return LogType.TYPE_OPERATE;
            case "定时任务":
                return LogType.TYPE_CRONJOB;
            default:
                return LogType.TYPE_UNKNOW;
        }
    }

    public static LogType fromLogtype(int logtype) {
        switch (logtype) {
            case 1:
                return LogType.TYPE_LOGIN;
            case 2:
                return LogType.TYPE_OPERATE;
            case 3:
                return LogType.TYPE_CRONJOB;
            default:
                return LogType.TYPE_UNKNOW;
        }
    }
}
