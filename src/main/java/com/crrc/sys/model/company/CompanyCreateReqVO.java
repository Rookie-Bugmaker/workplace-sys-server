package com.crrc.sys.model.company;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CompanyCreateReqVO {
    @TableId(
            value = "id",
            type = IdType.AUTO
    )
    @Schema(
            description = "id",
            example = "1"
    )
    private Long id;
    @Schema(
            description = "公司名称",
            example = "中车长客"
    )
    @NotNull(message = "公司名称不能为空")
    private String companyName;
    @Schema(
            description = "公司id",
            example = "2000"
    )
    @NotNull(message = "公司id不能为空")
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
            description = "备注",
            example = "备注"
    )
    private String remark;
}
