package com.crrc.sys.model.server;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(description = "服务管理 - 修改服务相关信息")
public class ServiceUpdateReqVO {
    @NotNull(message = "服务id不能为空")
    private Long id;

    /**
     * 服务中文名称
     */
    private String nameCn;

    /**
     * 系统描述
     */
    private String description;

    /**
     * 服务状态 0 正常 1下线
     */
    private String status;

}
