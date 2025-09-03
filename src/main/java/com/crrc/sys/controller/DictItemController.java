package com.crrc.sys.controller;


import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.annotation.AuditLog;
import com.crrc.sys.cache.RedisCache;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.dict.Dict;
import com.crrc.sys.entity.dict.DictItem;
import com.crrc.sys.enums.LogType;
import com.crrc.sys.enums.OperateType;
import com.crrc.sys.listener.CommonProcessExcelListener;
import com.crrc.sys.model.dict.DictItemExcelVO;
import com.crrc.sys.model.dict.DictItemListReqVO;
import com.crrc.sys.model.dict.DictItemPageReqVO;
import com.crrc.sys.model.dict.DictListReqVO;
import com.crrc.sys.service.DictCacheService;
import com.crrc.sys.service.impl.DictItemServiceImpl;
import com.crrc.sys.service.impl.DictServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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


/**
 * <p>
 * 字典项 前端控制器
 * </p>
 *
 * @author lijiangtao
 * @since 2025-01-10
 */
@RestController
@RequestMapping("dict/item")
@Tag(name = "字典项", description = "字典项")
public class DictItemController {

    @Autowired
    private DictItemServiceImpl dictItemService;

    @Autowired
    private DictServiceImpl dictService;

    @Autowired
    private DictCacheService dictCacheService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisCache redisCache;

    @GetMapping("/page")
    @Operation(summary = "获取字典项列表 page")
    @AuditLog(value = "获取字典项列表 page", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_QUERY)
    public IPage page(DictItemPageReqVO pageReqVO) {
        return dictItemService.page(pageReqVO);
    }

    @GetMapping("/query")
    @Operation(summary = "获取字典项列表")
    @AuditLog(value = "获取字典项列表", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_QUERY)
    public List<DictItem> query(DictItemListReqVO queryReqVO) {
        return dictItemService.query(queryReqVO);
    }

    @GetMapping("/get")
    @Operation(summary = "获得字典项")
    @AuditLog(value = "获得字典项", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_QUERY)
    @Parameter(name = "id", description = "主键编号", required = true, example = "1")
    public DictItem get(@RequestParam Long id) {
        return dictItemService.getById(id);
    }


    @PostMapping("/create")
    @Operation(summary = "创建字典项")
    @AuditLog(value = "创建字典项", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_ADD)
    public Boolean create(@Valid @RequestBody DictItem createReqVO) {
        LambdaQueryWrapper<DictItem> wrapper = new LambdaQueryWrapper<DictItem>()
                .eq(DictItem::getItemCode, createReqVO.getItemCode())
                .eq(DictItem::getDictId, createReqVO.getDictId());
        if(dictItemService.count(wrapper) > 0){
            return false;
        }
        Dict dict = dictService.getById(createReqVO.getDictId());
        if (dict == null) {
            return false;
        }

        boolean result = dictItemService.save(createReqVO);
        if (result) {
            dictCacheService.setDictItemCache(createReqVO, dict);
        }
        return result;
    }


    @PostMapping("/update")
    @Operation(summary = "更新字典项")
    @AuditLog(value = "更新字典项", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_MODIFY)
    public Boolean update(@Valid @RequestBody DictItem updateReqVO) {
        Dict dict = dictService.getById(updateReqVO.getDictId());
        if (dict == null) {
            return false;
        }

        if (updateReqVO.getId() != null) {
            DictItem oldItem = dictItemService.getById(updateReqVO.getId());
            if (oldItem != null && !StringUtils.equals(oldItem.getItemCode(), updateReqVO.getItemCode())) {
                // 字典项编码变更，需要清理旧缓存
                dictCacheService.deleteDictItemCache(oldItem, dict);
            }
        }

        boolean result = dictItemService.saveOrUpdate(updateReqVO);
        if (result) {
            dictCacheService.setDictItemCache(updateReqVO, dict);
        }
        return result;
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除字典项")
    @AuditLog(value = "删除字典项", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_DELETE)
    public Boolean delete(@NotNull @RequestParam Long id) {
        DictItem dictItem = dictItemService.getById(id);
        if (dictItem == null) {
            return false;
        }

        Dict dict = dictService.getById(dictItem.getDictId());
        boolean result = dictItemService.removeById(id);
        if (result && dict != null) {
            dictCacheService.deleteDictItemCache(dictItem, dict);
        }
        return result;
    }
    @GetMapping("/export")
    @AuditLog(value = "导出字典项", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_EXPORT)
    @Operation(summary = "导出字典项")
    public void export(@Validated @RequestBody(required = false) Dict exportReqVO,
                              HttpServletResponse response) throws IOException {
        String type = "xlsx";
        String description = "字典项";
        String exportName = description + new SimpleDateFormat("yyyyMMddhhmmss").format(System.currentTimeMillis());
        ExcelTypeEnum typeEnum = "xlsx".equals(type) ? ExcelTypeEnum.XLSX : ExcelTypeEnum.XLS;
        String contentType = "csv".equals(type) ? "text/csv;charset=UTF-8" :
                ("xls".equals(type) ? "application/vnd.ms-excel" : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        try {
            List<Dict> dictList = dictService.query(BeanUtils.toBean(exportReqVO, DictListReqVO.class));
            List<DictItemExcelVO> list = dictItemService.getDictItemExcelVOs(dictList);
            String fileName = URLEncoder.encode((exportName), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + "." + type);
            EasyExcel.write(response.getOutputStream(), DictItemExcelVO.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .autoCloseStream(true)
                    .excelType(typeEnum)
                    .sheet(description)
                    .doWrite(list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/import")
    @Operation(summary = "导入字典项")
    public Boolean imports(MultipartFile file) throws IOException {
        CommonProcessExcelListener<DictItemExcelVO, Boolean> readExcelListener;
        readExcelListener = new CommonProcessExcelListener<>(dictItemService::saveDictItemExcelVO);
        EasyExcel.read(file.getInputStream(), DictItemExcelVO.class, readExcelListener).sheet().doRead();

        if (!readExcelListener.getResult()) {
            return false;
        }
        dictCacheService.batchSetDictItemCache(readExcelListener.getData());
        return readExcelListener.getResult();
    }
}
