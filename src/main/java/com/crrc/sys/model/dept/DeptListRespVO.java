package com.crrc.sys.model.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "部门管理 - 获取部门列表 Resp VO")
public class DeptListRespVO {
    @Schema(
            description = "部门id",
            example = "1"
    )
    private Long id;
    @Schema(description = "组织编码", example = "2")
    private String orgCode;

    @Schema(description = "父组织编码", example = "2")
    private String parentOrgCode;
    @Schema(
            description = "部门名称",
            example = "流程与信息化"
    )
    @NotNull(message = "部门名称不能为空")
    private String departmentName;
    @Schema(
            description = "部门排序",
            example = "0"
    )
    private Integer departmentOrder = 0;
    @Schema(
            description = "部门状态",
            example = "0（可用）"
    )
    private String status;
    @Schema(description = "所属公司id", example = "1")
    private Long companyId;
    @Schema(
            description = "备注",
            example = "备注"
    )
    private String remark;

    List<DeptListRespVO> children = new ArrayList<>();
}
