package com.crrc.sys.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.annotation.AuditLog;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.company.Company;
import com.crrc.sys.entity.iddesign.IdDesign;
import com.crrc.enums.LogType;
import com.crrc.enums.OperateType;
import com.crrc.sys.listener.CommonProcessExcelListener;
import com.crrc.sys.listener.DictAndItemImportListener;
import com.crrc.sys.listener.IdDesignImportListener;
import com.crrc.sys.model.dict.DictItemExcelVO;
import com.crrc.sys.model.dict.DictListReqVO;
import com.crrc.sys.model.iddesign.IdDesignExcelVO;
import com.crrc.sys.model.iddesign.IdDesignListReqVO;
import com.crrc.sys.model.iddesign.IdDesignPageReqVO;
import com.crrc.sys.model.iddesign.IdRequest;
import com.crrc.sys.service.CompanyService;
import com.crrc.sys.service.impl.DesignIdCompanyServiceImpl;
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
import java.util.ArrayList;
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

    @Autowired
    private DesignIdCompanyServiceImpl designIdCompanyService;

    @Autowired
    private CompanyService companyService;

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
                .set(IdDesign::getLastResetDate, updateReqVO.getLastResetDate())
                .set(IdDesign::getEnableCompany, updateReqVO.getEnableCompany())
                .eq(IdDesign::getId, updateReqVO.getId());
        return idDesignService.update(queryWrapper);
    }

    @Transactional
    @DeleteMapping("/delete")
    @Operation(summary = "删除自定义ID实体")
    @AuditLog(value = "删除自定义ID实体", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_DELETE)
    public Boolean deleteIdDesign(@NotNull @RequestParam Long id) {
        // 先删除公司隔离数据
        designIdCompanyService.deleteByDesignId(id);
        return idDesignService.removeById(id);
    }

    @GetMapping("/download/export")
    @AuditLog(value = "导出自定义ID报表", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_EXPORT)
    @Operation(summary = "导出自定义ID报表")
    public void exportIdDesign(IdDesignListReqVO exportReqVO, HttpServletResponse response) throws IOException {
        String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = URLEncoder.encode("流水号报表-" + new SimpleDateFormat("yyyyMMddhhmmss")
                        .format(System.currentTimeMillis()), "UTF-8")
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        exportReqVO = (exportReqVO == null) ? new IdDesignListReqVO() : exportReqVO;
        List<IdDesign> idDesignList = idDesignService.queryExcel(exportReqVO);
        ExcelTypeEnum typeEnum = ExcelTypeEnum.XLSX;
        EasyExcel.write(response.getOutputStream(), IdDesignExcelVO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .autoCloseStream(true)
                .excelType(typeEnum)
                .sheet("流水号报表")
                .doWrite(idDesignList);
    }

    @GetMapping("/download/template")
    @AuditLog(value = "导出自定义ID报表模板", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_EXPORT)
    @Operation(summary = "导出自定义ID报表模板")
    public void exportTemplateIdDesign(HttpServletResponse response) throws IOException {
        String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = URLEncoder.encode("流水号报表模板", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        ExcelTypeEnum typeEnum = ExcelTypeEnum.XLSX;
        EasyExcel.write(response.getOutputStream(), IdDesignExcelVO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .autoCloseStream(true)
                .excelType(typeEnum)
                .sheet("流水号报表")
                .doWrite(new ArrayList<>());
    }

    @PostMapping("/import")
    @AuditLog(value = "导入自定义ID报表", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_IMPORT)
    @Operation(summary = "导入自定义ID报表")
    public Boolean imports(MultipartFile file) throws IOException {
        IdDesignImportListener listener = new IdDesignImportListener(idDesignService);
        EasyExcel.read(file.getInputStream(), IdDesignExcelVO.class, listener)
                .headRowNumber(1)
                .sheet()
                .doRead();
        return true;
    }

    @PostMapping("/nextId")
    @Operation(summary = "获得对应业务标识的下一个id-openfeign接口")
    public String nextId(@RequestBody IdRequest request) {
        if(StringUtils.isBlank(request.getBusinessId())){
            throw new BizException("流水号生成错误-错误业务标识不能为空");
        }
        if(request.getCompanyId() != null && !isExistedCompanyById(request.getCompanyId())) {
            throw new BizException("流水号生成错误-ID为"+request.getCompanyId()+"的公司不存在");
        }
        return idDesignService.safeNextId(request.getBusinessId(),
                request.getPositionToParamMap(),
                request.getCompanyId());
    }

    private Boolean isExistedCompanyById(Long companyId) {
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Company::getId, companyId);
        return companyService.exists(wrapper);
    }

}
