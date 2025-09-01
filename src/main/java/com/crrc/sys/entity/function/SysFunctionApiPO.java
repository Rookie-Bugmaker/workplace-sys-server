package com.crrc.sys.entity.function;

import com.crrc.sys.entity.BaseEntityYunLing;

/**
 * 功能接口绑定信息
 *
 * @author 谭永强
 * @date 2024-01-11
 */
public class SysFunctionApiPO extends BaseEntityYunLing {

    /**
     * 功能ID
     */
    private Long functionId;
    /**
     * 接口ID
     */
    private Long apiId;

    public SysFunctionApiPO() {
    }

    public SysFunctionApiPO(Long functionId, Long apiId) {
        this.functionId = functionId;
        this.apiId = apiId;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }
}
