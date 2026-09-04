package com.crrc.sys.model.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "部门简略信息")
public class DeptSimpleRespVO {

    @Schema(description = "部门ID")
    private Long id;

    @Schema(description = "部门编码")
    private String code;

    @Schema(description = "部门名称")
    private String name;
}