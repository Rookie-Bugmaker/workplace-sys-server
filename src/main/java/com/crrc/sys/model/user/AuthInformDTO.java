package com.crrc.sys.model.user;

import java.util.List;
import java.util.Map;

public class AuthInformDTO {
    private Map<String, List<String>> data;
    private Boolean isOr;

    public Boolean getIsOr() {
        return isOr;
    }
    public void setIsOr(Boolean isOr) {
        this.isOr = isOr;
    }

    public Map<String, List<String>> getData() {
        return data;
    }

    public void setData(Map<String, List<String>> data) {
        this.data = data;
    }
}