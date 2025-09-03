package com.crrc.sys.model.dict;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class DictItemListReqVO {
    @Schema(description = "字典项编码")
    private String itemCode;

    @Schema(description = "字典项名称")
    private String itemName;

    @Schema(description = "序列")
    private String sorted;
}
