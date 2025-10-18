package com.crrc.sys.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.annotation.AuditLog;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.iddesign.IdDesign;
import com.crrc.sys.enums.LogType;
import com.crrc.sys.enums.OperateType;
import com.crrc.sys.listener.CommonProcessExcelListener;
import com.crrc.sys.model.dict.DictItemExcelVO;
import com.crrc.sys.model.dict.DictListReqVO;
import com.crrc.sys.model.iddesign.IdDesignExcelVO;
import com.crrc.sys.model.iddesign.IdDesignListReqVO;
import com.crrc.sys.model.iddesign.IdDesignPageReqVO;
import com.crrc.sys.model.iddesign.IdRequest;
import com.crrc.sys.service.impl.IdDesignServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 自定义ID 前端控制器
 * </p>
 *
 * @author lijiangtao
 * @since 2025-07-11 11:55:04
 */
@RestController
@RequestMapping("idDesign")
@Tag(name = "自定义ID", description = "自定义ID")
public class IdDesignController {

    @Autowired
    private IdDesignServiceImpl idDesignService;

    @GetMapping("/page")
    @Operation(summary = "获取自定义ID列表 page")
    @AuditLog(value = "获取自定义ID列表 page", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_QUERY)
    public PaginationGrid<IdDesign> pageIdDesign(IdDesignPageReqVO pageReqVO) {
        return idDesignService.page(pageReqVO);
    }

    @GetMapping("/query")
    @Operation(summary = "获取自定义ID列表")
    @AuditLog(value = "获取自定义ID列表", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_QUERY)
    public List<IdDesign> queryIdDesign(IdDesignListReqVO queryReqVO) {
        return idDesignService.query(queryReqVO);
    }

    @GetMapping("/get")
    @Operation(summary = "获得自定义ID实体")
    @AuditLog(value = "获得自定义ID实体", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_QUERY)
    public IdDesign getIdDesign(@RequestParam Long id) {
        return idDesignService.getById(id);
    }

    @Transactional
    @PostMapping("/create")
    @Operation(summary = "创建自定义ID实体")
    @AuditLog(value = "创建自定义ID实体", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_ADD)
    public Long createIdDesign(@Valid @RequestBody IdDesign createReqVO) {
        LambdaQueryWrapper<IdDesign> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IdDesign::getBusinessIdentifier, createReqVO.getBusinessIdentifier());
        if (idDesignService.count(queryWrapper) > 0) {
            throw new BizException("业务标识已存在");
        }
        idDesignService.save(createReqVO);
        return createReqVO.getId();
    }


    @Transactional
    @PostMapping("/update")
    @Operation(summary = "更新自定义ID实体")
    @AuditLog(value = "更新自定义ID实体", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_MODIFY)
    public Boolean updateIdDesign(@Valid @RequestBody IdDesign updateReqVO) {
        LambdaUpdateWrapper<IdDesign> queryWrapper = new LambdaUpdateWrapper<>();
        // 不能更新业务标识、流水号起始值和流水号当前值
        queryWrapper.set(IdDesign::getSpliceSeparator, updateReqVO.getSpliceSeparator())
                .set(IdDesign::getUniqueCodeType, updateReqVO.getUniqueCodeType())
                .set(IdDesign::getSerialNumberDigits, updateReqVO.getSerialNumberDigits())
                .set(IdDesign::getUniqueCodePosition, updateReqVO.getUniqueCodePosition())
                .set(IdDesign::getEnableTime, updateReqVO.getEnableTime())
                .set(IdDesign::getTimePosition, updateReqVO.getTimePosition())
                .set(IdDesign::getTimePrecision, updateReqVO.getTimePrecision())
                .set(IdDesign::getTimeFormat, updateReqVO.getTimeFormat())
                .set(IdDesign::getStringPosition, updateReqVO.getStringPosition())
                .set(IdDesign::getCustomString, updateReqVO.getCustomString())
                .set(IdDesign::getEnableString, updateReqVO.getEnableString())
                .set(IdDesign::getEnableCycle, updateReqVO.getEnableCycle())
                .set(IdDesign::getCycleUnit, updateReqVO.getCycleUnit())
                .set(IdDesign::getCycleDate, updateReqVO.getCycleDate())
                .eq(IdDesign::getId, updateReqVO.getId());
        return idDesignService.update(queryWrapper);
    }

    @Transactional
    @DeleteMapping("/delete")
    @Operation(summary = "删除自定义ID实体")
    @AuditLog(value = "删除自定义ID实体", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_DELETE)
    public Boolean deleteIdDesign(@NotNull @RequestParam Long id) {
        return idDesignService.removeById(id);
    }

    @GetMapping("/export")
    @AuditLog(value = "导出自定义ID报表", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_EXPORT)
    @Operation(summary = "导出自定义ID报表")
    public void exportIdDesign(IdDesignListReqVO exportReqVO, HttpServletResponse response) throws IOException {
        String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = URLEncoder.encode("自定义ID报表-" + new SimpleDateFormat("yyyyMMddhhmmss")
                        .format(System.currentTimeMillis()), "UTF-8")
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        exportReqVO = (exportReqVO == null) ? new IdDesignListReqVO() : exportReqVO;
        List<IdDesign> idDesignList = idDesignService.query(exportReqVO);
        ExcelTypeEnum typeEnum = ExcelTypeEnum.XLSX;
        EasyExcel.write(response.getOutputStream(), IdDesignExcelVO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .autoCloseStream(true)
                .excelType(typeEnum)
                .sheet("自定义ID报表")
                .doWrite(idDesignList);
    }

    @PostMapping("/import")
    @AuditLog(value = "导入自定义ID报表", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_IMPORT)
    @Operation(summary = "导入自定义ID报表")
    public Boolean imports(MultipartFile file) throws IOException {
        CommonProcessExcelListener<IdDesign, Boolean> readExcelListener;
        readExcelListener = new CommonProcessExcelListener<>(idDesignService::saveBatch);
        EasyExcel.read(file.getInputStream(), IdDesign.class, readExcelListener)
                .headRowNumber(1)
                .sheet()
                .doRead();
        return true;
    }

    @PostMapping("/nextId")
    @Operation(summary = "获得对应业务标识的下一个id-openfeign接口")
    public String nextId(@RequestBody IdRequest request) {
        if(StringUtils.isBlank(request.getBusinessId())){
            throw new BizException("业务标识不能为空");
        }
        return idDesignService.safeNextId(request.getBusinessId(), request.getPositionToParamMap());
    }

}
