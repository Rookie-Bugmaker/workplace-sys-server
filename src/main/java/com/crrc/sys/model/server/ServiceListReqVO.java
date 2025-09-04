package com.crrc.sys.model.server;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "服务管理 - 获取服务列表")
public class ServiceListReqVO {
    @Schema(
            description = "服务名称",
            example = "MES"
    )
    private String name;

    @Schema(
            description = "服务名称",
            example = "主服务"
    )
    private String nameCn;

    @Schema(
            description = "系统名称",
            example = "demo"
    )
    private String sysName;
    @Schema(
            description = "服务状态",
            example = "1"
    )
    private String status;

    @Schema(
            description = "是否是系统服务",
            example = "true"
    )
    private Boolean sysService;
}
