package com.crrc.sys.model.company;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema(description = "公司管理 - 删除公司下的用户 Req VO")
public class CompanyUserDeleteReqVO {
    @Schema(
            description = "公司id",
            example = "1"
    )
    @NotNull(message = "公司id不能为空")
    private Long companyId;

    @Schema(
            description = "用户id列表",
            example = "[1,2,3]"
    )
    @NotEmpty(message = "用户id列表不能为空")
    private List<Long> userIds;
}
