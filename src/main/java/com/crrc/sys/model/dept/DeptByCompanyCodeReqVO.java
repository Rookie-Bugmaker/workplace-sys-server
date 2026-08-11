package com.crrc.sys.model.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(description = "根据公司编码查询部门请求")
public class DeptByCompanyCodeReqVO {

    @NotNull(message = "公司编码不能为空")
    @Schema(description = "公司编码", example = "COMP001")
    private String companyCode;

    @Schema(description = "是否返回树形结构", example = "true")
    private Boolean isTree;
}