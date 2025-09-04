package com.crrc.sys.entity.parameter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "sys_global_setting_value")
public class GlobalSettingValue {
    @Id
    private String id;
    
    @Field("setting_id")
    private String settingId;
    
    @Field("item_results")
    private List<GlobalSettingItemResult> itemResults;

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSettingId() {
        return settingId;
    }

    public void setSettingId(String settingId) {
        this.settingId = settingId;
    }

    public List<GlobalSettingItemResult> getItemResults() {
        return itemResults;
    }

    public void setItemResults(List<GlobalSettingItemResult> itemResults) {
        this.itemResults = itemResults;
    }
}