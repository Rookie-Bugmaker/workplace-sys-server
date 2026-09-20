package com.crrc.sys.model.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "字典VO")
public class DictListReqVO {

    @Schema(description = "主键")
    private Long id;
    @Schema(description = "字典编码")
    private String dictCode;

    @Schema(description = "字典名称")
    private String dictName;

    @Schema(description = "字典类型 0：系统 1：业务")
    private String dictType;

    @Schema(description = "说明", example = "demo")
    private String remark;

    @Schema(description = "项目编码（空为公共字典）")
    private String projectCode;
}
