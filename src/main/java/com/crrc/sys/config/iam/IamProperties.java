package com.crrc.sys.config.iam;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "iam")
public class IamProperties {
    private String appKey;
    private String appSecret;
    private Map<String, String> projectMap;

    // getters and setters
    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public Map<String, String> getProjectMap() {
        return projectMap;
    }

    public void setProjectMap(Map<String, String> projectMap) {
        this.projectMap = projectMap;
    }
}