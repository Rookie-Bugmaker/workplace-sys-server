package com.crrc.sys.model.project;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class ProjectPageReqVO {


    /**
     * 项目名
     */
    @Schema(description = "项目名称 对应服务管理中的系统名称" ,example = "1")
    private String name;

    /**
     * 项目中文名称
     */
    @Schema(description = "项目中文名称" ,example = "1")
    private String nameCn;

    @Schema(description = "项目状态" ,example = "1")
    private String status;
    private static final Integer PAGE_NO = 0;
    private static final Integer PAGE_SIZE = 10;


    @Min(value = 0, message = "页码最小值为 0")
    @Schema(description = "页码，从 0 开始", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @Parameter(name = "pageNo", description = "页码", required = false, example = "0")
    private Integer pageNo = PAGE_NO;


    @Min(value = 1, message = "每页条数最小值为 1")
    @Max(value = 100, message = "每页条数最大值为 100")
    @Schema(description = "每页条数，最大值为 100", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer pageSize = PAGE_SIZE;
}
