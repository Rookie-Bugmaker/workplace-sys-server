package com.crrc.sys.model.dict;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class DictItemListReqVO {

    @JsonSerialize(using = ToStringSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "所属字典主键")
    private Long dictId;

    @Schema(description = "字典项编码")
    private String itemCode;

    @Schema(description = "字典项名称")
    private String itemName;

    @Schema(description = "序列")
    private String sorted;
}
