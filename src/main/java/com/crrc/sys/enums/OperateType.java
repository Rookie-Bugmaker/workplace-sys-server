package com.crrc.sys.enums;

public enum OperateType {

    TYPE_NULL(0, "未知类型"),

    TYPE_LOGIN(1, "登录"),

    TYPE_LOGOUT(2, "登出"),

    TYPE_ADD(3, "添加"),

    TYPE_MODIFY(4, "修改"),

    TYPE_DELETE(5, "删除"),

    TYPE_QUERY(6, "查询"),

    TYPE_IMPORT(7, "导入"),

    TYPE_EXPORT(8, "导出"),


    ;


    private int operateType;

    private String description;

    OperateType(int logType, String description) {
        this.operateType = logType;
        this.description = description;
    }

    public int getLogType() {
        return this.operateType;
    }

    public String getDescription() {
        return this.description;
    }

    public static OperateType fromDescription(String description) {
        switch (description) {
            case "登录":
                return OperateType.TYPE_LOGIN;
            case "登出":
                return OperateType.TYPE_LOGOUT;
            case "添加":
                return OperateType.TYPE_ADD;
            case "修改":
                return OperateType.TYPE_MODIFY;
            case "删除":
                return OperateType.TYPE_DELETE;
            case "查询":
                return OperateType.TYPE_QUERY;
            case "导入":
                return OperateType.TYPE_IMPORT;
            case "导出":
                return OperateType.TYPE_EXPORT;
            default:
                return OperateType.TYPE_NULL;
        }
    }

    public static OperateType fromOperateType(int operateType) {
        switch (operateType) {
            case 1:
                return OperateType.TYPE_LOGIN;
            case 2:
                return OperateType.TYPE_LOGOUT;
            case 3:
                return OperateType.TYPE_ADD;
            case 4:
                return OperateType.TYPE_MODIFY;
            case 5:
                return OperateType.TYPE_DELETE;
            case 6:
                return OperateType.TYPE_QUERY;
            case 7:
                return OperateType.TYPE_IMPORT;
            case 8:
                return OperateType.TYPE_EXPORT;
            default:
                return OperateType.TYPE_NULL;
        }
    }
}
