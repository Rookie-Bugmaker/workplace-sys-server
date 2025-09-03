package com.crrc.sys.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.annotation.AuditLog;
import com.crrc.sys.cache.RedisCache;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.dict.Dict;
import com.crrc.sys.entity.dict.DictItem;
import com.crrc.sys.enums.LogType;
import com.crrc.sys.enums.OperateType;
import com.crrc.sys.model.company.CompanyGetRespVO;
import com.crrc.sys.model.dict.DictExcelVO;
import com.crrc.sys.model.dict.DictListReqVO;
import com.crrc.sys.model.dict.DictPageReqVO;
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
    public IPage pageDict(DictPageReqVO pageReqVO) {
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
    public Boolean createDict(@Valid @RequestBody Dict createReqVO) {
        if(dictService.count(new LambdaQueryWrapper<Dict>().eq(Dict::getDictCode, createReqVO.getDictCode())) > 0){
            return false;
        }
        boolean result = dictService.save(createReqVO);
        if (result) {
            dictCacheService.setDictCache(createReqVO);
        }
        return result;
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
            return false;
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
    @PostMapping("/export")
    @Operation(summary = "导出字典")
    @AuditLog(value = "导出字典", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_EXPORT)
    public void exportDict(@Validated @RequestBody(required = false) DictExcelVO exportReqVO, HttpServletResponse response){
        // 实现导出方法
        dictService.exports(exportReqVO, response);
    }


    @PostMapping("/import")
    @Operation(summary = "导入字典")
    @AuditLog(value = "导出字典", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_IMPORT)
    public Boolean imports(MultipartFile file) {
        // 实现导入读取方法
        List<DictExcelVO> importReqVO = dictService.reads(file);
        Set<String> codeSet = importReqVO.stream().map(DictExcelVO::getDictCode).collect(Collectors.toSet());
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<Dict>().in(Dict::getDictCode, codeSet);
        if(dictService.count(wrapper) > 0 || importReqVO == null || importReqVO.size() == 0) {
            return false;
        }
        importReqVO.forEach(data -> {
            dictCacheService.setDictCache(BeanUtils.toBean(data, Dict.class));
        });
        return true;
    }
}
