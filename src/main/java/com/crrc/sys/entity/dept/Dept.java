package com.crrc.sys.entity.dept;

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
@TableName("sys_department")
@Schema(description = "部门")
public class Dept extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelIgnore
    @Schema(description = "部门id", example = "1")
    private Long id;

    @Schema(description = "组织编码", example = "2")
    private String orgCode;

    @Schema(description = "父组织编码", example = "2")
    private String parentOrgCode;

    @Schema(description = "部门名称", example = "流程与信息化")
    private String departmentName;

    @Schema(description = "部门排序", example = "0")
    private Integer departmentOrder;

    @Schema(description = "部门状态", example = "0（可用）")
    private String status;

    @Schema(description = "所属公司id", example = "1")
    private Long companyId;

    @Schema(description = "备注", example = "备注")
    private String remark;

    /**
     * 是否删除
     */
    @TableLogic(value = "0",delval = "id")
    @Schema(description = "删除", example = "false")
    private Integer deleted;
}
