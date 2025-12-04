package com.crrc.sys.model.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "服务管理 - 项目管理 - 返回分页项目列表")
public class ProjectPageRespVO {
    @Schema(description = "项目id", example = "1")
    private Long id;

    /**
     * 项目名
     */
    @Schema(description = "项目名称 对应服务管理中的系统名称", example = "1")
    private String name;

    /**
     * 项目中文名称
     */
    @Schema(description = "项目中文名称", example = "1")
    private String nameCn;

    /**
     * 前端访问url
     */
    @Schema(description = "项目入口url", example = "1")
    private String url;

    /**
     * 项目描述
     */
    @Schema(description = "项目描述", example = "1")
    private String description;

    @Schema(description = "项目状态", example = "1")
    private String status;


    /**
     * 图形符号
     */
    @Schema(description = "图形符号" ,example = "1")
    private String graphicSymbol;

    /**
     * 归属分组
     */
    @Schema(description = "归属分组" ,example = "1")
    private String ownershipGroup;

    private List<ServiceEntity> ServiceList = new ArrayList<>();

    @Data
    @Schema(description = "项目关联服务")
    public static class ServiceEntity {

        @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private String id;

        @Schema(description = "服务名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "服务名称")
        private String nameCn;

    }
}
