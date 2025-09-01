package com.cccr.sys.entity.function;

import java.util.List;

/**
 * 按钮-API权限
 *
 * @author 谭永强
 * @date 2024-07-13
 */
public class BtnApiBO {

    /**
     * 按钮名称
     */
    private String name;
    /**
     * API权限集合
     */
    private List<String> apiUriList;

    public BtnApiBO(String name, List<String> apiUriList) {
        this.name = name;
        this.apiUriList = apiUriList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getApiUriList() {
        return apiUriList;
    }

    public void setApiUriList(List<String> apiUriList) {
        this.apiUriList = apiUriList;
    }
}
