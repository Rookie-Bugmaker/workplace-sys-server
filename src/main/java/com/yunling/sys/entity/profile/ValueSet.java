package com.yunling.sys.entity.profile;

/**
 * 值集集合-实体
 * @author 谭永强
 * @date 2022-01-12
 */
public class ValueSet {

    /**
     * ID
     */
    private String id;
    /**
     * 编码
     */
    private String code;
    /**
     * 描述
     */
    private String desc;
    /**
     * 父级ID
     */
    private String parentId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
