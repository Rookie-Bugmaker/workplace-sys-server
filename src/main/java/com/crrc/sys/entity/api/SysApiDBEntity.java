package com.crrc.sys.entity.api;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;

/**
 * API实体-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-13
 */
@TableName("sys_api")
public class SysApiDBEntity extends BaseEntityYunLing {

    /**
     * 主键
     */
    @TableId(type = IdType.INPUT)
    private Long apiId;
    /**
     * 接口名称
     */
    private String apiName;
    /**
     * 接口URI
     */
    private String apiUri;
    /**
     * 请求方式(GET/POST)
     */
    private String apiMethod;
    /**
     * 接口描述
     */
    private String apiDesc;

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiUri() {
        return apiUri;
    }

    public void setApiUri(String apiUri) {
        this.apiUri = apiUri;
    }

    public String getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(String apiMethod) {
        this.apiMethod = apiMethod;
    }

    public String getApiDesc() {
        return apiDesc;
    }

    public void setApiDesc(String apiDesc) {
        this.apiDesc = apiDesc;
    }
}
