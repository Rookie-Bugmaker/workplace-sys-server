package com.crrc.sys.entity.company;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_company")
@Schema(description = "公司")
public class Company extends BaseEntity {
    private static final long serialVersionUID = -6001228062442034980L;
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelIgnore
    @Schema(description = "id", example = "1")
    private Long id;

    @Schema(description = "公司名称", example = "中车长客")
    private String companyName;

    @Schema(description = "公司id", example = "2000")
    private Long companyId;

    @Schema(description = "组织编码", example = "2")
    private String orgCode;

    @Schema(description = "工厂编码", example = "2000")
    private String plantCode;

    @Schema(description = "公司状态", example = "0（可用）")
    private String status;

    @Schema(description = "备注", example = "备注")
    private String remark;

    /**
     * 是否删除
     */
    @TableLogic(value = "0",delval = "id")
    @Schema(description = "删除", example = "false")
    private Integer deleted;
}
