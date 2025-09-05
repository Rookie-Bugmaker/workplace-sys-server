package com.crrc.sys.model.iddesign;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.crrc.sys.entity.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * <p>
 * 自定义ID
 * </p>
 *
 * @author lijiangtao
 * @since 2025-07-11 11:55:04
 */
@Data
public class IdDesignListReqVO extends BaseEntity {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @Schema(description = "业务标识", example = "")
    private String businessIdentifier;

    @Schema(description = "唯一编码类型（0 UUID 1雪花ID 2流水号）", example = "")
    private String uniqueCodeType;


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
