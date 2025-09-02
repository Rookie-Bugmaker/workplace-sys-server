package com.crrc.sys.model.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema(description = "部门管理 - 获取部门 Resp VO")
public class DeptRespVO {
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
    private Integer departmentOrder;
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

    private List<Role> roles;

    @Data
    @Schema(description = "权限")
    public static class Role {

        @Schema(description = "角色id", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
        private String id;


        @Schema(description = "角色名", requiredMode = Schema.RequiredMode.REQUIRED, example = "角色名称")
        private String name;

    }
}
