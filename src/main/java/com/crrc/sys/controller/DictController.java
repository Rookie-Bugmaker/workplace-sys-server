package com.crrc.sys.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.annotation.AuditLog;
import com.crrc.sys.cache.RedisCache;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.common.CollectionUtils;
import com.crrc.sys.common.CustomFileUtils;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.entity.dict.Dict;
import com.crrc.sys.entity.dict.DictItem;
import com.crrc.enums.LogType;
import com.crrc.enums.OperateType;
import com.crrc.sys.listener.DictAndItemImportListener;
import com.crrc.sys.model.dict.*;
import com.crrc.sys.service.DictCacheService;
import com.crrc.sys.service.impl.DictItemServiceImpl;
import com.crrc.sys.service.impl.DictServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 字典 前端控制器
 * </p>
 *
 * @author lijiangtao
 * @since 2025-01-10
 */
@Tag(name = "字典", description = "字典")
@Slf4j
@RestController
@RequestMapping("dict")
public class DictController {
    @Autowired
    private DictServiceImpl dictService;

    @Autowired
    private DictItemServiceImpl dictItemService;

    @Autowired
    private DictCacheService dictCacheService;

    @GetMapping("/page")
    @Operation(summary = "获取字典列表 page")
    @AuditLog(value = "获取字典列表 page", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_QUERY)
    public PaginationGrid<Dict> pageDict(DictPageReqVO pageReqVO) {
        return dictService.page(pageReqVO);
    }

    @GetMapping("/query")
    @Operation(summary = "获取字典列表")
    @AuditLog(value = "获取字典列表", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_QUERY)
    public List<Dict> queryDict(DictListReqVO queryReqVO) {
        return dictService.query(queryReqVO);
    }

    @GetMapping("/get")
    @Operation(summary = "获得字典")
    @AuditLog(value = "获得字典", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_QUERY)
    public Dict getDict(@RequestParam Long id) {
        return dictService.getById(id);
    }

    @PostMapping("/create")
    @Operation(summary = "创建字典")
    @AuditLog(value = "创建字典", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_ADD)
    public Long createDict(@Valid @RequestBody Dict createReqVO) {
        if(dictService.count(new LambdaQueryWrapper<Dict>().eq(Dict::getDictCode, createReqVO.getDictCode())) > 0){
            throw new BizException("字典已存在");
        }
        boolean result = dictService.save(createReqVO);
        if (result) {
            dictCacheService.setDictCache(createReqVO);
        }
        return result ? createReqVO.getId() : null;
    }

    @PostMapping("/update")
    @Operation(summary = "更新字典")
    @AuditLog(value = "更新字典", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_MODIFY)
    public Boolean updateDict(@Valid @RequestBody Dict updateReqVO) {
        if (updateReqVO.getId() != null) {
            Dict oldDict = dictService.getById(updateReqVO.getId());
            if (oldDict != null && !StringUtils.equals(oldDict.getDictCode(), updateReqVO.getDictCode())) {
                // 字典编码变更，需要清理旧缓存
                dictCacheService.deleteDictCache(oldDict);
                // 为新字典编码创建缓存（包括字典项）
                dictCacheService.setDictCache(updateReqVO);
                // 重新加载字典项缓存
                List<DictItem> items = dictItemService.getItemByMainId(updateReqVO.getId());
                dictCacheService.loadDictItemCache(updateReqVO, items);
            }
        }

        boolean result = dictService.saveOrUpdate(updateReqVO);
        if (result && (updateReqVO.getId() == null ||
                !StringUtils.equals(dictService.getById(updateReqVO.getId()).getDictCode(), updateReqVO.getDictCode()))) {
            // 如果是新增或者字典编码没有变更的情况，设置缓存
            dictCacheService.setDictCache(updateReqVO);
        }
        return result;
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除字典")
    @AuditLog(value = "删除字典", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_DELETE)
    public Boolean deleteDict(@NotNull @RequestParam Long id) {
        Dict dict = dictService.getById(id);
        if (dict == null) {
            throw new BizException("字典不存在");
        }
        // 先删除关联的字典项
        LambdaUpdateWrapper<DictItem> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(DictItem::getDictId, id);
        dictItemService.remove(queryWrapper);

        // 再删除字典
        boolean result = dictService.removeById(id);
        if (result) {
            dictCacheService.deleteDictCache(dict);
        }
        return result;
    }
    @GetMapping("/download/export")
    @Operation(summary = "导出字典")
    @AuditLog(value = "导出字典", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_EXPORT)
    public void exportDict(DictExcelVO exportReqVO, HttpServletResponse response){
        // 实现导出方法
        dictService.exports(exportReqVO, response);
    }

    @GetMapping("/download/template")
    @Operation(summary = "导出模板")
    @AuditLog(value = "导出模板", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_EXPORT)
    public void templateDict(HttpServletResponse response){
        // 实现导出方法
        dictService.template(response);
    }

    @PostMapping("/import")
    @Operation(summary = "导入字典")
    @AuditLog(value = "导出字典", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_IMPORT)
    public Boolean imports(MultipartFile file) {
        // 实现导入读取方法
        List<DictExcelVO> importReqVO = dictService.reads(file);
        Set<String> codeSet = importReqVO.stream().map(DictExcelVO::getDictCode).collect(Collectors.toSet());
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<Dict>().in(Dict::getDictCode, codeSet);
        if(dictService.count(wrapper) > 0) {
            throw new BizException("存在重复字典编码");
        }
        if(importReqVO == null || importReqVO.size() == 0) {
            throw new BizException("不存在可以导入的字典数据");
        }
        importReqVO.forEach(data -> {
            dictCacheService.setDictCache(BeanUtils.toBean(data, Dict.class));
        });
        return true;
    }

    @PostMapping("/importAll")
    @Operation(summary = "导入字典及字典项")
    @AuditLog(value = "导入字典及字典项", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_IMPORT)
    public Boolean importAllDictAndItems(MultipartFile file) throws IOException {
        DictAndItemImportListener listener = new DictAndItemImportListener(dictService, dictItemService, dictCacheService);
        EasyExcel.read(file.getInputStream(), DictAndItemExcelVO.class, listener).sheet().doRead();
        return true;
    }

    @GetMapping("/download/templateAll")
    @Operation(summary = "下载字典及字典项导入模板")
    public void downloadAllTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("字典及字典项导入模板", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), DictAndItemExcelVO.class)
                .sheet("字典数据")
                .doWrite(new ArrayList<>());
    }

    @GetMapping("/download/exportAll")
    @Operation(summary = "导出字典及字典项")
    public void exportAllTemplate(DictExcelVO exportReqVO, HttpServletResponse response) throws IOException {
        List<Dict> dictList = dictService.query(exportReqVO);
        List<DictAndItemExcelVO> exportList = new ArrayList<>();
        dictList.forEach(dict -> {
            DictVO dictVO = convertToDictVO(dict);
            dictVO.getItems().forEach(item -> {
                DictAndItemExcelVO exportData = new DictAndItemExcelVO();
                exportData.setDictCode(dictVO.getDictCode());
                exportData.setDictName(dictVO.getDictName());
                exportData.setDictType(dictVO.getDictType());
                exportData.setDictRemark(dictVO.getRemark());
                exportData.setItemCode(item.getItemCode());
                exportData.setItemName(item.getItemName());
                exportData.setItemSorted(item.getSorted());
                exportList.add(exportData);
            });
        });
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("字典及字典项数据", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), DictAndItemExcelVO.class)
                .sheet("字典数据")
                .doWrite(exportList);
    }

    // 转换方法
    private DictVO convertToDictVO(Dict dict) {
        DictVO dictVO = new DictVO();
        org.springframework.beans.BeanUtils.copyProperties(dict, dictVO);

        // 加载关联的字典项
        List<DictItem> items = dictItemService.getItemByMainId(dict.getId());
        dictVO.setItems(items.stream()
                .map(this::convertToDictItemVO)
                .collect(Collectors.toList()));

        return dictVO;
    }

    private DictItemVO convertToDictItemVO(DictItem item) {
        DictItemVO itemVO = new DictItemVO();
        org.springframework.beans.BeanUtils.copyProperties(item, itemVO);
        return itemVO;
    }
}
