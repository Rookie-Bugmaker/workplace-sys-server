package com.crrc.sys.entity.parameter;

public class GlobalSettingItem {
    private String settingItemId;
    private String settingItemName;
    private Integer settingItemType; // 0:字符串, 1:整数, 2:小数
    private Integer settingItemSorted;

    // getters and setters
    public String getSettingItemId() {
        return settingItemId;
    }

    public void setSettingItemId(String settingItemId) {
        this.settingItemId = settingItemId;
    }

    public String getSettingItemName() {
        return settingItemName;
    }

    public void setSettingItemName(String settingItemName) {
        this.settingItemName = settingItemName;
    }

    public Integer getSettingItemType() {
        return settingItemType;
    }

    public void setSettingItemType(Integer settingItemType) {
        this.settingItemType = settingItemType;
    }

    public Integer getSettingItemSorted() {
        return settingItemSorted;
    }

    public void setSettingItemSorted(Integer settingItemSorted) {
        this.settingItemSorted = settingItemSorted;
    }
}