package com.crrc.sys.entity.org.supplier;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;

/**
 * 组织架构-供应商
 *
 * @author 谭永强
 * @date 2022-12-09
 */
public class SysOrgSupplierPageParam extends BaseEntityYunLing {

    /**
     * 供应商ID
     */
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
     * 当前页码
     */
    private Integer page;
    /**
     * 偏移量，当pageSize=0时查询所有数据
     */
    private Integer pageSize;

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

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
