package com.crrc.sys.model.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "项目简略信息")
public class ProjectSimpleRespVO {

    @Schema(description = "项目ID")
    private Long id;

    @Schema(description = "项目编码")
    private String code;

    @Schema(description = "项目名称")
    private String name;
}
