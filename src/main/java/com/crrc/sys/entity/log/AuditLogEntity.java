package com.crrc.sys.entity.log;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.convert.LogTypeConverter;
import com.crrc.sys.convert.OperationTypeConverter;
import com.crrc.enums.LogType;
import com.crrc.enums.OperateType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "审计日志实体类")
@TableName(value = "sys_audit_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@ContentRowHeight(10)
@HeadRowHeight(20)
@ColumnWidth(25)
public class AuditLogEntity implements Serializable {

    /**
     * 日志id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelIgnore
    @Schema(description = "日志id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    /**
     * 用户名
     */
    @ExcelProperty(value = "用户名", index = 0)
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    private String userName;

    /**
     * 方法名称
     */
    @ExcelProperty(value = "方法名称", index = 1)
    @Schema(description = "方法名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "get")
    private String funcName;

    /**
     * 日志类型
     */
    @ExcelProperty(value = "日志类型", index = 2, converter = LogTypeConverter.class)
    @Schema(description = "日志类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "TYPE_OPERATE")
    private LogType logType;

    /**
     * 操作类型
     */
    @ExcelProperty(value = "操作类型", index = 3, converter = OperationTypeConverter.class)
    @Schema(description = "操作类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "TYPE_ADD")
    private OperateType operationType;

    /**
     * IP地址
     */
    @ExcelProperty(value = "IP地址", index = 4)
    @Schema(description = "ip地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    private String remoteIp;

    /**
     * 运行时长
     */
    @ExcelProperty(value = "运行时长（毫秒）", index = 5)
    @Schema(description = "运行时长（毫秒）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000")
    private Long runTime;

    /**
     * 结果
     */
    @ExcelProperty(value = "结果", index = 6)
    @Schema(description = "结果", requiredMode = Schema.RequiredMode.REQUIRED, example = "成功")
    private String result;

    @ExcelProperty(value = "系统名称", index = 7)
    @Schema(description = "系统名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "workplace")
    private String sysName;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间", index = 8)
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "202x-01-01 00:00:00")
    private LocalDateTime createDate;

    /**
     * SQL操作详情
     */
    @ExcelProperty(value = "SQL操作详情", index = 9)
    @Schema(description = "SQL操作详情", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String sqlModify;

    /**
     * 业务单据编码
     */
    @ExcelProperty(value = "业务单据编码", index = 10)
    @Schema(description = "业务单据编码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String businessCode;

    /**
     * 业务单据名称
     */
    @ExcelProperty(value = "业务单据名称", index = 11)
    @Schema(description = "业务单据名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String businessName;

    /**
     * 业务ID
     */
    @ExcelProperty(value = "业务ID", index = 12)
    @Schema(description = "业务ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String businessId;

    public AuditLogEntity(String userName, String funcName, String remoteIp, LogType logType, OperateType operationType, Long runTime, String result, String sysName) {
        this.userName = userName;
        this.funcName = funcName;
        this.remoteIp = remoteIp;
        this.logType = logType;
        this.operationType = operationType;
        this.runTime = runTime;
        this.result = result;
        this.sysName = sysName;
    }

    public AuditLogEntity(String userName, String funcName, String remoteIp, LogType logType, OperateType operationType,
                          Long runTime, String result, String sysName, String sqlModify, String businessCode,
                          String businessName, String businessId) {
        this.userName = userName;
        this.funcName = funcName;
        this.remoteIp = remoteIp;
        this.logType = logType;
        this.operationType = operationType;
        this.runTime = runTime;
        this.result = result;
        this.sysName = sysName;
        this.sqlModify = sqlModify;
        this.businessCode = businessCode;
        this.businessName = businessName;
        this.businessId = businessId;
    }
}