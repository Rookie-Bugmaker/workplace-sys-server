package com.crrc.sys.entity.org.supplier;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;

import java.util.Date;

/**
 * 组织架构-供应商
 *
 * @author 谭永强
 * @date 2022-12-09
 */
@TableName("sys_org_supplier")
public class SysOrgSupplier{

    /**
     * 供应商ID
     */
    @TableId(type = IdType.INPUT)
    private Long supplierId;
    /**
     * 主数据ID
     */
    private String mainDataId;
    /**
     * 供应商编码
     */
    private String supplierCode;
    /**
     * 工厂编码
     */
    private String plantCode;
    /**
     * 组织编码
     */
    private String orgCode;
    /**
     * 人员状态(1:在职、2:离职)
     */
    private Integer status;

    /**
     * 创建人
     */
    private Long createBy;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 最后修改人
     */
    private Long lastUpdateBy;
    /**
     * 最后修改时间
     */
    private Date lastUpdateDate;

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getPlantCode() {
        return plantCode;
    }

    public void setPlantCode(String plantCode) {
        this.plantCode = plantCode;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMainDataId() {
        return mainDataId;
    }

    public void setMainDataId(String mainDataId) {
        this.mainDataId = mainDataId;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(Long lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}
