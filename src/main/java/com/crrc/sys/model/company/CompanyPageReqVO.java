package com.crrc.sys.model.company;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class CompanyPageReqVO {
    @Schema(
            description = "公司名称",
            example = "中车长客"
    )
    private String companyName;
    @Schema(
            description = "公司id",
            example = "2000"
    )
    private Long companyId;

    @Schema(
            description = "组织code",
            example = "2000"
    )
    @NotNull(message = "组织Code不能为空")
    private String orgCode;

    @Schema(description = "工厂编码", example = "2000")
    private String plantCode;
    @Schema(
            description = "公司状态",
            example = "0（可用）"
    )
    private String status;
    private static final Integer PAGE_NO = 0;
    private static final Integer PAGE_SIZE = 10;


    @Min(value = 0, message = "页码最小值为 0")
    @Schema(description = "页码，从 0 开始", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @Parameter(name = "pageNo", description = "页码", required = false, example = "0")
    private Integer page = PAGE_NO;


    @Min(value = 1, message = "每页条数最小值为 1")
    @Max(value = 100, message = "每页条数最大值为 100")
    @Schema(description = "每页条数，最大值为 100", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer pageSize = PAGE_SIZE;
}
