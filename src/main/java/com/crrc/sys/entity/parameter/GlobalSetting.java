package com.crrc.sys.entity.parameter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "sys_global_setting")
public class GlobalSetting {
    @Id
    private String settingId;
    
    @Field("setting_name")
    private String settingName;
    
    @Field("setting_items")
    private List<GlobalSettingItem> settingItems;

    // getters and setters
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

    public List<GlobalSettingItem> getSettingItems() {
        return settingItems;
    }

    public void setSettingItems(List<GlobalSettingItem> settingItems) {
        this.settingItems = settingItems;
    }
}