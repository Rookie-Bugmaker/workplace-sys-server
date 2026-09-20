package com.crrc.sys.model.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量配置字典项目归属入参
 *
 * @author lijiangtao
 * @since 2026-09-05
 */
@Data
@Schema(description = "批量配置字典项目归属入参")
public class DictBatchAssignReqVO {

    @Schema(description = "操作的字典编码列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "字典编码列表不能为空")
    private List<String> dictCodes;

    @Schema(description = "目标项目编码（空串或null表示公共字典）")
    private String targetProjectCode;

    @Schema(description = "是否移动模式：true=移动（改归属），false=复制（克隆，默认）")
    private Boolean moveMode = false;
}
