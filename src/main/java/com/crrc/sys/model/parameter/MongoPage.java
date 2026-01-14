package com.crrc.sys.model.parameter;

public class MongoPage {
    private int page = 1;    // 当前页码，默认第1页
    private int pageSize = 10;  // 每页显示条数，默认10条
    private String sortField;   // 排序字段
    private String sortOrder;   // 排序方式(asc/desc)
    private String settingId;   // 新增：用于模糊查询settingId
    private String settingName; // 新增：用于模糊查询settingName

    private String settingItemId;   // 新增：用于模糊查询settingItemId

    private String settingItemContent; // 新增：用于模糊查询settingItemContent

    public int getPage() {
        return page;
    }

    public void setPage(int pageNum) {
        this.page = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSettingId() {
        return settingId;
    }

    public void setSettingId(String settingId) {
        this.settingId = settingId;
    }

    public String getSettingName() {
        return settingName;
    }

    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }

    public String getSettingItemId() {
        return settingItemId;
    }

    public void setSettingItemId(String settingItemId) {
        this.settingItemId = settingItemId;
    }

    public String getSettingItemContent() {
        return settingItemContent;
    }

    public void setSettingItemContent(String settingItemContent) {
        this.settingItemContent = settingItemContent;
    }
}