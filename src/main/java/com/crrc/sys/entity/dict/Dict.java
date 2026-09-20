package com.crrc.sys.entity.dict;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.baomidou.mybatisplus.annotation.*;
import com.crrc.sys.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 字典
 * </p>
 *
 * @author lijiangtao
 * @since 2025-01-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_dict")
@Schema(description = "字典")
public class Dict extends BaseEntity {

    private static final long serialVersionUID = -6001228062442034981L;

    @Schema(description = "主键")
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @Schema(description = "备注", example = "demo")
    private String remark;

    @Schema(description = "组织ID", example = "demo")
    private Long orgId;

    @TableLogic(value = "0")
    @Schema(description = "删除", example = "false")
    private Integer deleted;

    @Schema(description = "字典编码")
    private String dictCode;

    @Schema(description = "字典名称")
    private String dictName;

    @Schema(description = "字典类型 0：系统 1：业务")
    private String dictType;

    @Schema(description = "项目编码（空为公共字典，所有项目可处理）")
    private String projectCode;

}
