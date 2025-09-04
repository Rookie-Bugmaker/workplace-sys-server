package com.crrc.sys.model.server;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "获取系统列表")
public class ServiceSysListRespVO {
    /**
     * 系统名称
     */
    private String sysName;

    private Boolean sysService;

    private String status;
}
