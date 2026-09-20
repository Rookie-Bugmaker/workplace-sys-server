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
import com.crrc.sys.entity.project.ProjectEntity;
import com.crrc.sys.listener.DictAndItemImportListener;
import com.crrc.sys.model.dict.*;
import com.crrc.sys.service.DictCacheService;
import com.crrc.sys.service.ProjectService;
import com.crrc.sys.service.SysDataAuthService;
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

    @Autowired
    private SysDataAuthService dataAuthService;

    @Autowired
    private ProjectService projectService;

    /**
     * 数据权限配置编码：p_data_auth 的 value 为当前用户可访问的项目编码列表。
     */
    private static final String DATA_AUTH_PROJECT_CODE = "p_data_auth";

    /**
     * 取当前用户可访问的项目编码集合（p_data_auth 的 value 列表）。
     * 空集合表示用户没有任何项目权限（只能处理公共字典）。
     */
    private Set<String> getAllowedProjectCodes() {
        Map<String, List<String>> dataAuthMap = dataAuthService.getDataAuth();
        if (dataAuthMap == null || dataAuthMap.isEmpty()) {
            return Collections.emptySet();
        }
        List<String> values = dataAuthMap.get(DATA_AUTH_PROJECT_CODE);
        List<Long> projectIdList = values.stream().map(Long::valueOf).collect(Collectors.toList());
        List<String> projectCodeList = projectService.findByIdList(projectIdList).stream().map(ProjectEntity::getName).collect(Collectors.toList());
        return projectCodeList == null ? Collections.emptySet() : new HashSet<>(projectCodeList);
    }

    /**
     * 校验当前用户对指定字典是否有处理权限：
     * 公共字典（projectCode 为空）恒放行；项目字典需用户拥有该项目数据权限。
     */
    private void checkProjectAuth(Dict dict) {
        if (dict == null || StringUtils.isBlank(dict.getProjectCode())) {
            return;
        }
        Set<String> allowed = getAllowedProjectCodes();
        if (!allowed.contains(dict.getProjectCode())) {
            throw new BizException("无该项目的数据权限：" + dict.getProjectCode());
        }
    }

    /**
     * 校验当前用户对指定项目编码是否有权限（用于入参场景）。
     * 公共字典（projectCode 为空）恒放行。
     */
    private void checkProjectCode(String projectCode) {
        if (StringUtils.isBlank(projectCode)) {
            return;
        }
        Set<String> allowed = getAllowedProjectCodes();
        if (!allowed.contains(projectCode)) {
            throw new BizException("无该项目的数据权限：" + projectCode);
        }
    }

    @GetMapping("/page")
    @Operation(summary = "获取字典列表 page")
    @AuditLog(value = "获取字典列表 page", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_QUERY)
    public PaginationGrid<Dict> pageDict(DictPageReqVO pageReqVO) {
        return dictService.page(pageReqVO, getAllowedProjectCodes());
    }

    @GetMapping("/query")
    @Operation(summary = "获取字典列表")
    @AuditLog(value = "获取字典列表", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_QUERY)
    public List<Dict> queryDict(DictListReqVO queryReqVO) {
        return dictService.query(queryReqVO, getAllowedProjectCodes());
    }

    @GetMapping("/get")
    @Operation(summary = "获得字典")
    @AuditLog(value = "获得字典", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_QUERY)
    public Dict getDict(@RequestParam Long id) {
        Dict dict = dictService.getById(id);
        checkProjectAuth(dict);
        return dict;
    }

    @PostMapping("/create")
    @Operation(summary = "创建字典")
    @AuditLog(value = "创建字典", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_ADD)
    public Long createDict(@Valid @RequestBody Dict createReqVO) {
        if(dictService.count(new LambdaQueryWrapper<Dict>().eq(Dict::getDictCode, createReqVO.getDictCode())) > 0){
            throw new BizException("字典已存在");
        }
        checkProjectCode(createReqVO.getProjectCode());
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
        // 1. 参数校验与权限校验
        if (updateReqVO.getId() != null) {
            // 更新操作：获取旧数据，校验新旧项目权限
            Dict oldDict = dictService.getById(updateReqVO.getId());
            checkProjectAuth(oldDict);
            checkProjectCode(updateReqVO.getProjectCode());
        } else {
            // 新增操作：校验项目权限
            checkProjectCode(updateReqVO.getProjectCode());
        }

        // 2. 执行数据库更新操作
        boolean result = dictService.saveOrUpdate(updateReqVO);

        // 3. 仅在数据库操作成功后处理缓存
        if (result) {
            if (updateReqVO.getId() == null) {
                // 新增成功：直接设置缓存
                dictCacheService.setDictCache(updateReqVO);
            } else {
                // 更新成功：处理缓存失效与更新
                Dict oldDict = dictService.getById(updateReqVO.getId()); // 或从第一步获取
                // 关键：判断编码是否实际发生了变更
                if (oldDict != null && !StringUtils.equals(oldDict.getDictCode(), updateReqVO.getDictCode())) {
                    // 编码已变更：删除旧编码缓存
                    dictCacheService.deleteDictCache(oldDict);
                }
                // 无论编码是否变更，都更新/设置当前数据的缓存（最终状态）
                dictCacheService.setDictCache(updateReqVO);
                // 更新字典项缓存
                List<DictItem> items = dictItemService.getItemByMainId(updateReqVO.getId());
                dictCacheService.loadDictItemCache(updateReqVO, items);
            }
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
        checkProjectAuth(dict);
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

    @PostMapping("/rebuildCache")
    @Operation(summary = "清除并重建字典缓存")
    @AuditLog(value = "清除并重建字典缓存", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_MODIFY)
    public Boolean rebuildDictCache(@RequestParam String dictCode) {
        if (StringUtils.isBlank(dictCode)) {
            throw new BizException("字典编码不能为空");
        }
        Dict dict = dictService.getByCode(dictCode);
        if (dict == null) {
            throw new BizException("字典不存在");
        }
        checkProjectAuth(dict);
        // 1. 清除旧缓存（字典数据、索引、关联字典项）
        dictCacheService.deleteDictCache(dict);
        // 2. 重建字典缓存（字典数据 + 索引）
        dictCacheService.setDictCache(dict);
        // 3. 重建字典项缓存
        List<DictItem> items = dictItemService.getItemByMainId(dict.getId());
        dictCacheService.loadDictItemCache(dict, items);
        log.info("字典缓存重建成功，dictCode: {}", dictCode);
        return true;
    }

    @GetMapping("/download/export")
    @Operation(summary = "导出字典")
    @AuditLog(value = "导出字典", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_EXPORT)
    public void exportDict(DictExcelVO exportReqVO, HttpServletResponse response){
        // 实现导出方法（仅导出当前用户可见的字典）
        dictService.exports(exportReqVO, response, getAllowedProjectCodes());
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
    @AuditLog(value = "导入字典", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_IMPORT)
    public Boolean imports(MultipartFile file) {
        // 1. 读取Excel数据
        List<DictExcelVO> importReqVO = dictService.reads(file);
        if (importReqVO == null || importReqVO.size() == 0) {
            throw new BizException("不存在可以导入的字典数据");
        }
        // 2. 数据权限校验 + 转换为实体
        List<Dict> dictList = new ArrayList<>();
        for (DictExcelVO data : importReqVO) {
            // 校验当前用户对导入字典所属项目的数据权限
            checkProjectCode(data.getProjectCode());
            dictList.add(BeanUtils.toBean(data, Dict.class));
        }
        // 3. 校验字典编码不重复（DB + 本次导入内部）
        Set<String> codeSet = dictList.stream().map(Dict::getDictCode).collect(Collectors.toSet());
        if (codeSet.size() != dictList.size()) {
            throw new BizException("导入数据中存在重复字典编码");
        }
        if (!codeSet.isEmpty()
                && dictService.count(new LambdaQueryWrapper<Dict>().in(Dict::getDictCode, codeSet)) > 0) {
            throw new BizException("存在重复字典编码");
        }
        // 4. 持久化到数据库
        boolean saveResult = dictService.saveBatch(dictList);
        if (!saveResult) {
            throw new BizException("导入字典失败");
        }
        // 5. 构建缓存
        dictList.forEach(dictCacheService::setDictCache);
        return true;
    }

    @PostMapping("/importAll")
    @Operation(summary = "导入字典及字典项")
    @AuditLog(value = "导入字典及字典项", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_IMPORT)
    public Boolean importAllDictAndItems(MultipartFile file) throws IOException {
        DictAndItemImportListener listener = new DictAndItemImportListener(dictService, dictItemService, dictCacheService, getAllowedProjectCodes());
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
        // 仅导出当前用户可见的字典（公共字典 + 有权限的项目字典）
        List<Dict> dictList = dictService.query(exportReqVO, getAllowedProjectCodes());
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
                exportData.setProjectCode(dictVO.getProjectCode());
                exportList.add(exportData);
            });
        });
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("字典及字典项数据", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), DictAndItemExcelVO.class)
                .sheet("字典数据")
                .doWrite(exportList);
    }

    @PostMapping("/batchAssignProject")
    @Operation(summary = "批量配置字典项目归属（移动/复制）")
    @AuditLog(value = "批量配置字典项目归属", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_MODIFY)
    public Boolean batchAssignProject(@Valid @RequestBody DictBatchAssignReqVO req) {
        // 所有不传递或传递目标项目为空都默认为公共字典
        if(req.getTargetProjectCode() != null && req.getTargetProjectCode().trim().isEmpty()){
            req.setTargetProjectCode(null);
        }
        if (req.getDictCodes() == null || req.getDictCodes().isEmpty()) {
            throw new BizException("字典编码列表不能为空");
        }
        String targetProjectCode = req.getTargetProjectCode();
        boolean moveMode = Boolean.TRUE.equals(req.getMoveMode());
        // 校验目标项目权限（目标为公共字典时放行）
        checkProjectCode(targetProjectCode);

        // 查出所有源字典
        List<Dict> sourceDicts = dictService.list(new LambdaQueryWrapper<Dict>()
                .in(Dict::getDictCode, req.getDictCodes()));
        if (sourceDicts.isEmpty()) {
            throw new BizException("未找到指定的字典");
        }
        for (Dict source : sourceDicts) {
            // 源字典权限校验：项目字典需在用户权限内
            checkProjectAuth(source);
        }

        for (Dict source : sourceDicts) {
            if (moveMode) {
                // 移动模式：直接修改源字典的 projectCode
                Dict oldCopy = new Dict();
                oldCopy.setId(source.getId());
                oldCopy.setDictCode(source.getDictCode());
                oldCopy.setProjectCode(source.getProjectCode());
                source.setProjectCode(targetProjectCode);

                LambdaUpdateWrapper<Dict> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.set(Dict::getDictCode, source.getDictCode());
                updateWrapper.set(Dict::getProjectCode, targetProjectCode);
                updateWrapper.eq(Dict::getId, source.getId());
                dictService.update(updateWrapper);
                // 重建缓存（dictCode 不变，projectCode 变化需刷新字典数据缓存）
                dictCacheService.deleteDictCache(oldCopy);
                dictCacheService.setDictCache(source);
                List<DictItem> items = dictItemService.getItemByMainId(source.getId());
                dictCacheService.loadDictItemCache(source, items);
            } else {
                // 复制模式：克隆新字典到目标项目，dictCode 冲突时重命名
                String newDictCode = generateUniqueDictCode(source.getDictCode(), targetProjectCode);
                Dict newDict = new Dict();
                newDict.setDictCode(newDictCode);
                newDict.setDictName(source.getDictName());
                newDict.setDictType(source.getDictType());
                newDict.setRemark(source.getRemark());
                newDict.setProjectCode(targetProjectCode);
                dictService.save(newDict);
                // 克隆字典项
                List<DictItem> sourceItems = dictItemService.getItemByMainId(source.getId());
                List<DictItem> newItems = new ArrayList<>();
                for (DictItem item : sourceItems) {
                    DictItem newItem = new DictItem();
                    newItem.setDictId(newDict.getId());
                    newItem.setItemCode(item.getItemCode());
                    newItem.setItemName(item.getItemName());
                    newItem.setSorted(item.getSorted());
                    newItem.setRemark(item.getRemark());
                    newItems.add(newItem);
                }
                if (!newItems.isEmpty()) {
                    dictItemService.saveBatch(newItems);
                }
                // 构建缓存
                dictCacheService.setDictCache(newDict);
                dictCacheService.loadDictItemCache(newDict, newItems);
            }
        }
        log.info("批量配置字典项目归属完成，目标项目: {}, 移动模式: {}, 数量: {}",
                targetProjectCode, moveMode, sourceDicts.size());
        return true;
    }

    /**
     * 复制模式下生成不冲突的 dictCode：优先使用原编码，冲突则追加目标项目编码后缀，仍冲突则跳过式重试。
     */
    private String generateUniqueDictCode(String originCode, String targetProjectCode) {
        if (dictService.count(new LambdaQueryWrapper<Dict>().eq(Dict::getDictCode, originCode)) == 0) {
            return originCode;
        }
        String renamed = originCode + "__" + StringUtils.defaultString(targetProjectCode, "public");
        if (dictService.count(new LambdaQueryWrapper<Dict>().eq(Dict::getDictCode, renamed)) == 0) {
            return renamed;
        }
        int suffix = 1;
        String candidate;
        do {
            candidate = renamed + "_" + suffix++;
        } while (dictService.count(new LambdaQueryWrapper<Dict>().eq(Dict::getDictCode, candidate)) > 0);
        return candidate;
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
