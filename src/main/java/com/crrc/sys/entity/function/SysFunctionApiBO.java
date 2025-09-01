package com.crrc.sys.entity.function;

import com.crrc.sys.entity.api.SysApi;

import java.util.List;

/**
 * 功能-API关系
 *
 * @author 谭永强
 * @date 2022-09-19
 */
public class SysFunctionApiBO {

    /**
     * 功能ID
     */
    private Long functionId;
    /**
     * 功能拥绑定的接口集合
     */
    private List<SysApi> apiList;

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public List<SysApi> getApiList() {
        return apiList;
    }

    public void setApiList(List<SysApi> apiList) {
        this.apiList = apiList;
    }
}