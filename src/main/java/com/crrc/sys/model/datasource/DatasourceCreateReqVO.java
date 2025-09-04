package com.crrc.sys.model.datasource;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "数据源管理 - 创建数据源 Request VO")
public class DatasourceCreateReqVO {
    @Schema(
            description = "主键id",
            example = "1"
    )
    private String id;

    @Schema(
            description = "数据源id（与租户名称相同）",
            example = "2001"
    )
    private String datasourceId;

    @Schema(
            description = "是否是主数据源",
            example = "false"
    )
    private boolean mainDatasource;

    @Schema(
            description = "jdbc url ",
            example = "jdbc:mysql://localhost:3306/xxx?characterEncoding=utf-8&serverTimezone=UTC"
    )
    private String url;


    @Schema(
            description = "所属的系统",
            example = "default"
    )
    private String sysName;

    @Schema(
            description = "是否需要分表",
            example = "true"
    )
    private boolean sharding;

    @Schema(
            description = "用户名",
            example = "root"
    )
    private String username;

    @Schema(
            description = "密码",
            example = "root"
    )
    private String password;

    @Schema(
            description = "数据库类型",
            example = "mysql"
    )
    private String databaseType;

    @Schema(
            description = "数据库驱动",
            example = "com.mysql.cj.jdbc.Driver"
    )
    private String driverClassName;

    /**
     * 数据源名称
     */
    private String datasourceName;
}
