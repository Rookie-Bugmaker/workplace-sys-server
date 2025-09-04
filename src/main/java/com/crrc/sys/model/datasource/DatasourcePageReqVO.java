package com.crrc.sys.model.datasource;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "数据源管理 - 获取数据源分页 Request VO")
public class DatasourcePageReqVO {
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
            description = "jdbc url 需要带{},会自动替换成租户id",
            example = "jdbc:mysql://localhost:3306/xxx_{}?characterEncoding=utf-8&serverTimezone=UTC"
    )
    private String url;

    @Schema(
            description = "用户名",
            example = "root"
    )
    private String username;

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

    private static final Integer PAGE_NO = 0;
    private static final Integer PAGE_SIZE = 10;


    @NotNull(message = "页码不能为空")
    @Min(value = 0, message = "页码最小值为 0")
    @Schema(description = "页码，从 0 开始", requiredMode = Schema.RequiredMode.REQUIRED,example = "1")
    private Integer pageNo = PAGE_NO;

    @NotNull(message = "每页条数不能为空")
    @Schema(description = "每页条数，最大值为 100", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer pageSize = PAGE_SIZE;
}
