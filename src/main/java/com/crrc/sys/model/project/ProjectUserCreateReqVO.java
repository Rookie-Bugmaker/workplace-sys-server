package com.crrc.sys.model.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema(description = "项目管理 -  关联用户项目")
public class ProjectUserCreateReqVO {
    @Schema(
            description = "项目id",
            example = "1"
    )
    @NotNull(message = "项目Id不能为空")
    private Long projectId;

    @Schema(
            description = "用户id",
            example = "[1,2,3]"
    )
    private List<Long> UserIds;
}
