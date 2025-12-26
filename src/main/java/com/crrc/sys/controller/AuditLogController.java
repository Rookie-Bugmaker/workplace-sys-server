package com.crrc.sys.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.annotation.AuditLog;
import com.crrc.enums.LogType;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.log.AuditLogEntity;
import com.crrc.enums.LogType;
import com.crrc.enums.OperateType;
import com.crrc.sys.model.log.AuditLogPageReqVO;
import com.crrc.sys.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("audit_log")
@Tag(name = "日志管理 - 审计日志")
public class AuditLogController {

    @Resource
    private AuditLogService auditLogService;

    @Operation(summary = "创建操作日志 - feign接口使用")
    @PostMapping("/create")
    public String createAuditLog(@RequestBody String auditlog) {
        AuditLogEntity auditLogEntity = JSON.parseObject(auditlog, AuditLogEntity.class);
        boolean result = auditLogService.save(auditLogEntity);
        if (!result) {
            throw new BizException("保存失败");
        }
        return "OK";
    }

    @Operation(summary = "分页查询操作日志")
    @GetMapping("/page")
    public PaginationGrid<AuditLogEntity> getAuditLogPage(@Valid AuditLogPageReqVO pageReqVO) {
        LambdaQueryWrapper<AuditLogEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(!Objects.isNull(pageReqVO.getLogType()), AuditLogEntity::getLogType, pageReqVO.getLogType())
                .eq(!Objects.isNull(pageReqVO.getOperateType()), AuditLogEntity::getOperationType, pageReqVO.getOperateType())
                .eq(StrUtil.isNotEmpty(pageReqVO.getUsername()), AuditLogEntity::getUserName, pageReqVO.getUsername());
        queryWrapper.orderByDesc(AuditLogEntity::getCreateDate);
        Page<AuditLogEntity> pageInfo = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        pageInfo = auditLogService.page(pageInfo, queryWrapper);
        return PaginationGrid.success(pageInfo);
    }


    @Operation(summary = "操作日志导出", parameters = {
            @Parameter(name = "page", description = "页码", required = false, example = "0"),
            @Parameter(name = "pageSize", description = "每页条数", required = false, example = "100"),
            @Parameter(name = "logType", description = "日志类型", required = false, examples = {
                    @ExampleObject(name = "未知", value = "TYPE_UNKNOW", description = "未知"),
                    @ExampleObject(name = "登录", value = "TYPE_LOGIN", description = "登录"),
                    @ExampleObject(name = "操作", value = "TYPE_OPERATE", description = "操作"),
                    @ExampleObject(name = "定时任务", value = "TYPE_CRONJOB", description = "定时任务")
            },
                    schema = @Schema(description = "TYPE_UNKNOW:未知,TYPE_LOGIN:登录,TYPE_OPERATE:操作,TYPE_CRONJOB:定时任务")
            ),
            @Parameter(name = "operateType", description = "操作类型", required = false, examples = {
                    @ExampleObject(name = "未知", value = "TYPE_NULL", description = "未知"),
                    @ExampleObject(name = "登录", value = "TYPE_LOGIN", description = "登录"),
                    @ExampleObject(name = "登出", value = "TYPE_LOGOUT", description = "登出"),
                    @ExampleObject(name = "创建", value = "TYPE_ADD", description = "创建"),
                    @ExampleObject(name = "修改", value = "TYPE_MODIFY", description = "修改"),
                    @ExampleObject(name = "删除", value = "TYPE_DELETE", description = "删除"),
                    @ExampleObject(name = "查询", value = "TYPE_QUERY", description = "查询"),
                    @ExampleObject(name = "导入", value = "TYPE_IMPORT", description = "导入"),
                    @ExampleObject(name = "导出", value = "TYPE_EXPORT", description = "导出")
            },
                    schema = @Schema(description = "TYPE_NULL:未知,TYPE_LOGIN:登录,TYPE_LOGOUT:登出,TYPE_ADD:创建," +
                            "TYPE_MODIFY:修改,TYPE_DELETE:删除,TYPE_QUERY:查询,TYPE_IMPORT:导入,TYPE_EXPORT:导出")
            ),
            @Parameter(name = "username", description = "用户名", required = false, example = "admin")
    })
    @GetMapping("/export")
    @AuditLog(value = "操作日志导出", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_EXPORT)
    public void exportAuditLogs(HttpServletResponse response,
                                @RequestParam(required = false) Integer pageNo,
                                @RequestParam(required = false) Integer pageSize,
                                @RequestParam(required = false) LogType logType,
                                @RequestParam(required = false) OperateType operateType,
                                @RequestParam(required = false) String username) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = URLEncoder.encode("操作日志-" + new SimpleDateFormat("yyyyMMddhhmmss").format(System.currentTimeMillis()), "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=" + fileName + ".xlsx");
        LambdaQueryWrapper<AuditLogEntity> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(!Objects.isNull(logType), AuditLogEntity::getLogType, logType)
                .eq(!Objects.isNull(operateType), AuditLogEntity::getOperationType, operateType)
                .eq(StrUtil.isNotEmpty(username), AuditLogEntity::getUserName, username);
        if (!Objects.isNull(pageNo) && !Objects.isNull(pageSize)) {
            Page<AuditLogEntity> pageInfo = new Page<>(pageNo, pageSize);
            auditLogService.page(pageInfo, queryWrapper);
            EasyExcel.write(response.getOutputStream(), AuditLogEntity.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("操作日志")
                    .doWrite(pageInfo.getRecords());
        } else {
            List<AuditLogEntity> auditLogEntitys = auditLogService.list(queryWrapper);
            EasyExcel.write(response.getOutputStream(), AuditLogEntity.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("操作日志")
                    .doWrite(auditLogEntitys);
        }
    }

}
