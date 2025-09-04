package com.crrc.sys.model.project;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "服务管理 - 项目管理")
public class ProjectListRespVO {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "项目id" ,example = "1")
    private Long id;

    /**
     * 项目名
     */
    @Schema(description = "项目名称 对应服务管理中的系统名称" ,example = "1")
    private String name;

    /**
     * 项目中文名称
     */
    @Schema(description = "项目中文名称" ,example = "1")
    private String nameCn;

    /**
     * 前端访问url
     */
    @Schema(description = "项目入口url" ,example = "1")
    private String url;

    /**
     * 项目描述
     */
    @Schema(description = "项目描述" ,example = "1")
    private String description;

    @Schema(description = "项目状态" ,example = "1")
    private String status;
}
