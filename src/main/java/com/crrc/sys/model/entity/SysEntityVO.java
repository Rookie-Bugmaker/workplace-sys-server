package com.crrc.sys.model.entity;

import java.util.Date;

/**
 * 业务实体PO
 *
 * @author 谭永强
 * @date 2024-11-13
 */
public class SysEntityVO {

    /**
     * 主键
     */
    private Long id;
    /**
     * 实体名称
     */
    private String name;
    /**
     * 实体编码
     */
    private String code;
    /**
     * 备注
     */
    private String desc;
    /**
     * 状态;启用:Y、禁用:N
     */
    private String status;
    /**
     * 最后修改时间
     */
    private Date lastUpdateDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}
