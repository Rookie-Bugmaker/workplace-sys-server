package com.crrc.sys.model.datasource;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "数据源管理 - 更新数据源密码 Req VO")
public class DatasourceUpdatePasswordReqVO {
    @Schema(
            description = "主键id",
            example = "1"
    )
    private String id;

    @Schema(
            description = "密码",
            example = "root"
    )
    private String password;
}
