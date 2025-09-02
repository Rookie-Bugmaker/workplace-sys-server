package com.crrc.sys.model.dept;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Schema(description = "部门管理 - 导入部门 Request VO")
@Accessors(chain = false)
public class DeptImportReqVO {
    @Schema(
            description = "部门名称",
            example = "流程与信息化"
    )
    @NotNull(message = "部门名称不能为空")
    @ExcelProperty(value = "部门名称", index = 2)
    private String departmentName;
    @Schema(
            description = "部门状态 0（可用） 1（停用）",
            example = "0"
    )
    @ExcelProperty(value = "部门状态", index = 3)
    private String status;
    @Schema(
            description = "备注",
            example = "备注"
    )
    @ExcelProperty(value = "备注", index = 4)
    private String remark;
}
