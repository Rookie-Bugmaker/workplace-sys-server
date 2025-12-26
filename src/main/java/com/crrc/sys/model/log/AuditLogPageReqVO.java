package com.crrc.sys.model.log;

import com.crrc.enums.LogType;
import com.crrc.enums.OperateType;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Schema(description = "审计日志 - 审计日志分页 Req VO")
@Data
public class AuditLogPageReqVO {
    @Schema(
            description = "日志类型 (TYPE_UNKNOW:未知,TYPE_LOGIN:登录,TYPE_OPERATE:操作,TYPE_CRONJOB:定时任务)",
            example = "TYPE_LOGIN"
    )
    private LogType logType;
    @Schema(
            description = "操作类型 (TYPE_NULL:未知,TYPE_LOGIN:登录,TYPE_LOGOUT:登出,TYPE_ADD:创建," +
                    "TYPE_MODIFY:修改,TYPE_DELETE:删除,TYPE_QUERY:查询,TYPE_IMPORT:导入,TYPE_EXPORT:导出)",
            example = "TYPE_LOGIN"
    )
    private OperateType operateType;

    @Schema(description = "用户名", example = "admin")
    private String username;

    private static final Integer PAGE_NO = 0;
    private static final Integer PAGE_SIZE = 10;


    @NotNull(message = "页码不能为空")
    @Min(value = 0, message = "页码最小值为 0")
    @Schema(description = "页码，从 0 开始", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @Parameter(name = "pageNo", description = "页码", required = false, example = "0")
    private Integer pageNo = PAGE_NO;

    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数最小值为 1")
    @Max(value = 100, message = "每页条数最大值为 100")
    @Schema(description = "每页条数，最大值为 100", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer pageSize = PAGE_SIZE;
}
