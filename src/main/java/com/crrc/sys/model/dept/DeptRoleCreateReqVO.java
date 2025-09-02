package com.crrc.sys.model.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema(description = "部门管理 -  关联部门角色")
public class DeptRoleCreateReqVO {
    @Schema(
            description = "部门id",
            example = "1"
    )
    @NotNull(message = "部门Id不能为空")
    private Long deptId;

    @Schema(
            description = "角色id",
            example = "[1,2,3]"
    )
    private List<Long> roleIds;

}
