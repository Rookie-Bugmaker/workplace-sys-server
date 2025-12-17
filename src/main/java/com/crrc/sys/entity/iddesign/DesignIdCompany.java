package com.crrc.sys.entity.iddesign;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 自定义ID-公司隔离
 * </p>
 *
 * @author lijiangtao
 * @since 2025-12-16 09:55:04
 */
@TableName("sys_design_id_company")
@Schema(description = "自定义ID-公司隔离")
public class DesignIdCompany {
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableLogic(value = "0")
    @Schema(description = "删除", example = "false")
    private Integer deleted;

    @Schema(description = "自定义ID", example = "")
    private Long designId;

    @Schema(description = "公司ID", example = "")
    private Long companyId;

    @Schema(description = "流水号当前值", example = "")
    private Integer serialNumberCurrent;

    public DesignIdCompany(Long designId, Long companyId, Integer serialNumberCurrent) {
        this.designId = designId;
        this.companyId = companyId;
        this.serialNumberCurrent = serialNumberCurrent;
    }

    public DesignIdCompany() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDesignId() {
        return designId;
    }

    public void setDesignId(Long designId) {
        this.designId = designId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Integer getSerialNumberCurrent() {
        return serialNumberCurrent;
    }

    public void setSerialNumberCurrent(Integer serialNumberCurrent) {
        this.serialNumberCurrent = serialNumberCurrent;
    }

    @Override
    public String toString() {
        return "DesignIdCompany{" +
                "id=" + id +
                ", designId=" + designId +
                ", companyId=" + companyId +
                ", serialNumberCurrent=" + serialNumberCurrent +
                '}';
    }
}
