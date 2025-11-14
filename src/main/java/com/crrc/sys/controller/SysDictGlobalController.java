package com.crrc.sys.controller;

import com.crrc.sys.common.ResponseResult;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.entity.dict.Dict;
import com.crrc.sys.entity.dict.DictItem;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.model.dict.DictItemVO;
import com.crrc.sys.model.dict.DictVO;
import com.crrc.sys.service.DictCacheService;
import com.crrc.sys.service.DictItemService;
import com.crrc.sys.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端全局字典方法 前端控制器
 * </p>
 *
 * @author lijiangtao
 * @since 2025-01-10
 */
@RestController
@RequestMapping("global/dict")
@Tag(name = "字典", description = "字典")
@Slf4j
public class SysDictGlobalController {
    @Autowired
    private DictCacheService dictCacheService;
    @Autowired
    private DictService dictService;
    @Autowired
    private DictItemService dictItemService;


    @GetMapping("/get")
    @Operation(summary = "获得字典")
    public ResponseResult<DictVO> getDict(@RequestParam("dictCode") String dictCode) {
        try {
            // 1. 尝试从缓存获取
            DictVO dictVO = dictCacheService.getDictFromCache(dictCode);
            if (dictVO != null) {
                sortDictItems(dictVO);
                return new ResponseResult<>(true, HttpStatus.OK.value(), "获取字典成功", dictVO);
            }

            // 2. 缓存不存在，从数据库加载
            Dict dict = dictService.getByCode(dictCode);
            if (dict == null) {
                return new ResponseResult<>(true, HttpStatus.OK.value(), "字典不存在", null);
            }

            // 3. 转换并缓存结果
            dictVO = convertToDictVO(dict);
            sortDictItems(dictVO);
            dictCacheService.cacheDict(dictVO);
            return new ResponseResult<>(true, HttpStatus.OK.value(), "获取字典成功", dictVO);
        } catch (Exception e) {
            log.error("获取字典异常，dictCode: {}", dictCode, e);
            return new ResponseResult<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "获取字典失败", null);
        }
    }


    @GetMapping("/getName")
    @Operation(summary = "获得字典名称")
    public ResponseResult<String> getDictName(@RequestParam("dictCode") String dictCode) {
        try {
            // 1. 从缓存获取字典名称
            String dictName = dictCacheService.getDictNameFromCache(dictCode);
            if (StringUtils.isNotBlank(dictName)) {
                return new ResponseResult<>(true, HttpStatus.OK.value(), "获得字典名称成功", dictName);
            }

            // 2. 缓存不存在，从数据库加载
            Dict dict = dictService.getByCode(dictCode);
            if (dict == null) {
                return new ResponseResult<>(true, HttpStatus.OK.value(), "字典不存在", null);
            }

            // 3. 缓存字典信息
            dictCacheService.cacheDict(convertToDictVO(dict));
            return new ResponseResult<>(true, HttpStatus.OK.value(), "获得字典名称成功", dict.getDictName());
        } catch (Exception e) {
            log.error("获取字典名称异常，dictCode: {}", dictCode, e);
            return new ResponseResult<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "获取字典名称失败", null);
        }
    }

    @GetMapping("/getCodes")
    @Operation(summary = "根据名称获得字典编码")
    public ResponseResult<List<String>> getDictCodes(@RequestParam("dictName") String dictName) {
        try {
            // 1. 从缓存获取字典编码列表
            List<String> codes = dictCacheService.getDictCodesFromCache(dictName);
            if (!codes.isEmpty()) {
                return new ResponseResult<>(true, HttpStatus.OK.value(), "获得字典编码成功", codes);
            }

            // 2. 缓存不存在，从数据库加载
            List<Dict> dicts = dictService.getByName(dictName);
            if (CollectionUtils.isEmpty(dicts)) {
                return new ResponseResult<>(true, HttpStatus.OK.value(), "没有匹配的字典", Collections.emptyList());
            }

            // 3. 缓存字典信息
            dicts.forEach(dict -> dictCacheService.cacheDict(convertToDictVO(dict)));
            return new ResponseResult<>(true, HttpStatus.OK.value(), "获得字典编码成功",
                    dicts.stream().map(Dict::getDictCode).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("根据名称获取字典编码异常，dictName: {}", dictName, e);
            return new ResponseResult<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "获取字典编码失败", Collections.emptyList());
        }
    }

    @GetMapping("/all")
    @Operation(summary = "获得所有字典")
    public ResponseResult<List<DictVO>> allDict() {
        try {
            // 1. 从缓存获取所有字典
            List<DictVO> dictVOs = dictCacheService.getAllDictsFromCache();
            if (!dictVOs.isEmpty()) {
                return new ResponseResult<>(true, HttpStatus.OK.value(), "获得字典成功", dictVOs);
            }

            // 2. 缓存不存在，从数据库加载
            List<Dict> dicts = dictService.getAll();
            if (CollectionUtils.isEmpty(dicts)) {
                return new ResponseResult<>(true, HttpStatus.OK.value(), "没有字典数据", Collections.emptyList());
            }

            // 3. 转换并缓存结果
            dictVOs = dicts.stream()
                    .map(this::convertToDictVO)
                    .peek(dictCacheService::cacheDict)
                    .collect(Collectors.toList());

            return new ResponseResult<>(true, HttpStatus.OK.value(), "获得字典成功", dictVOs);
        } catch (Exception e) {
            log.error("获取所有字典异常", e);
            return new ResponseResult<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "获取字典失败", Collections.emptyList());
        }
    }

    @GetMapping("/getItem")
    @Operation(summary = "获得字典项")
    public ResponseResult<DictItemVO> getDictItem(
            @RequestParam("dictCode") String dictCode,
            @RequestParam("itemCode") String itemCode) {
        try {
            // 1. 从缓存获取字典项
            DictItemVO itemVO = dictCacheService.getDictItemFromCache(dictCode, itemCode);
            if (itemVO != null) {
                return new ResponseResult<>(true, HttpStatus.OK.value(), "获得字典项成功", itemVO);
            }

            // 2. 缓存不存在，从数据库加载
            Dict dict = dictService.getByCode(dictCode);
            if (dict == null) {
                return new ResponseResult<>(true, HttpStatus.OK.value(), "字典不存在", null);
            }

            DictItem item = dictItemService.getItemByItemId(dict.getId(), itemCode);
            if (item == null) {
                return new ResponseResult<>(true, HttpStatus.OK.value(), "字典项不存在", null);
            }

            // 3. 转换并缓存结果
            itemVO = convertToDictItemVO(item);
            dictCacheService.cacheDictItem(dictCode, itemVO);
            return new ResponseResult<>(true, HttpStatus.OK.value(), "获得字典项成功", itemVO);
        } catch (Exception e) {
            log.error("获取字典项异常，dictCode: {}, itemCode: {}", dictCode, itemCode, e);
            return new ResponseResult<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "获取字典项失败", null);
        }
    }

    @GetMapping("/getItemName")
    @Operation(summary = "获得字典项名称")
    public ResponseResult<String> getDictItemName(
            @RequestParam("dictCode") String dictCode,
            @RequestParam("itemCode") String itemCode) {
        try {
            // 1. 从缓存获取字典项名称
            String itemName = dictCacheService.getDictItemNameFromCache(dictCode, itemCode);
            if (StringUtils.isNotBlank(itemName)) {
                return new ResponseResult<>(true, HttpStatus.OK.value(), "获得字典项名称成功", itemName);
            }

            // 2. 缓存不存在，从数据库加载
            Dict dict = dictService.getByCode(dictCode);
            if (dict == null) {
                return new ResponseResult<>(true, HttpStatus.OK.value(), "字典不存在", null);
            }

            DictItem item = dictItemService.getItemByItemId(dict.getId(), itemCode);
            if (item == null) {
                return new ResponseResult<>(true, HttpStatus.OK.value(), "字典项不存在", null);
            }

            // 3. 缓存字典项信息
            dictCacheService.cacheDictItem(dictCode, convertToDictItemVO(item));
            return new ResponseResult<>(true, HttpStatus.OK.value(), "获得字典项名称成功", item.getItemName());
        } catch (Exception e) {
            log.error("获取字典项名称异常，dictCode: {}, itemCode: {}", dictCode, itemCode, e);
            return new ResponseResult<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "获取字典项名称失败", null);
        }
    }

    @GetMapping("/getItemCode")
    @Operation(summary = "获得字典项编码")
    public ResponseResult<String> getDictItemCode(
            @RequestParam("dictCode") String dictCode,
            @RequestParam("itemName") String itemName) {
        try {
            // 1. 从缓存获取字典项编码
            String itemCode = dictCacheService.getDictItemCodeFromCache(dictCode, itemName);
            if (StringUtils.isNotBlank(itemCode)) {
                return new ResponseResult<>(true, HttpStatus.OK.value(), "获得字典项编码成功", itemCode);
            }

            // 2. 缓存不存在，从数据库加载
            Dict dict = dictService.getByCode(dictCode);
            if (dict == null) {
                return new ResponseResult<>(true, HttpStatus.OK.value(), "字典不存在", null);
            }

            DictItem item = dictItemService.getItemByItemName(dict.getId(), itemName);
            if (item == null) {
                return new ResponseResult<>(true, HttpStatus.OK.value(), "字典项不存在", null);
            }

            // 3. 缓存字典项信息
            dictCacheService.cacheDictItem(dictCode, convertToDictItemVO(item));
            return new ResponseResult<>(true, HttpStatus.OK.value(), "获得字典项编码成功", item.getItemCode());
        } catch (Exception e) {
            log.error("获取字典项编码异常，dictCode: {}, itemName: {}", dictCode, itemName, e);
            return new ResponseResult<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "获取字典项编码失败", null);
        }
    }

    @GetMapping("/getUserRealNameInDictExtends")
    @Operation(summary = "字典转化拓展-获得对应员工名称")
    public ResponseResult<String> getUserRealNameInDictExtends(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "userName", required = false) String userName) {
        if(userId == null && StringUtils.isBlank(userName)) {
            return new ResponseResult<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "参数不能同时为空", null);
        }
        try {
            // 1. 从缓存获取用户名称
            String realName = dictCacheService.getUserRealNameInDictExtendsFromCache(userId, userName);
            if (StringUtils.isNotBlank(realName)) {
                return new ResponseResult<>(true, HttpStatus.OK.value(), "获得用户名称成功", realName);
            }

            // 2. 缓存不存在，从数据库加载
            SysUser user = dictService.getUserRealNameInDictExtends(userId, userName);

            if (user == null) {
                return new ResponseResult<>(true, HttpStatus.OK.value(), "用户不存在", null);
            }

            // 3. 缓存字典项信息
            dictCacheService.cacheUserRealInDictExtends(user);
            return new ResponseResult<>(true, HttpStatus.OK.value(), "获得用户名称成功", user.getUserDescription());
        } catch (Exception e) {
            log.error("获取用户名称异常，userId: {}, userName: {}", userId, userName, e);
            return new ResponseResult<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "获取用户名称失败", null);
        }
    }

    @GetMapping("/getDeptNameNameInDictExtends")
    @Operation(summary = "字典转化拓展-获得对应部门名称")
    public ResponseResult<String> getDeptNameNameInDictExtends(
            @RequestParam(value = "deptId", required = false) Long deptId,
            @RequestParam(value = "orgCode", required = false) String orgCode) {
        if(deptId == null && StringUtils.isBlank(orgCode)) {
            return new ResponseResult<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "参数不能同时为空", null);
        }
        try {
            // 1. 从缓存获取部门名称
            String realName = dictCacheService.getDeptNameNameInDictExtendsFromCache(deptId, orgCode);
            if (StringUtils.isNotBlank(realName)) {
                return new ResponseResult<>(true, HttpStatus.OK.value(), "获得部门名称成功", realName);
            }

            // 2. 缓存不存在，从数据库加载
            Dept dept = dictService.getDeptNameNameInDictExtends(deptId, orgCode);

            if (dept == null) {
                return new ResponseResult<>(false, HttpStatus.OK.value(), "部门不存在", null);
            }

            // 3. 缓存字典项信息
            dictCacheService.cacheDeptNameInDictExtends(dept);
            return new ResponseResult<>(true, HttpStatus.OK.value(), "获得部门名称成功", dept.getDepartmentName());
        } catch (Exception e) {
            log.error("获取部门名称异常，deptId: {}, orgCode: {}", deptId, orgCode, e);
            return new ResponseResult<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "获取部门名称失败", null);
        }
    }

    // 转换方法
    private DictVO convertToDictVO(Dict dict) {
        DictVO dictVO = new DictVO();
        BeanUtils.copyProperties(dict, dictVO);

        // 加载关联的字典项
        List<DictItem> items = dictItemService.getItemByMainId(dict.getId());
        dictVO.setItems(items.stream()
                .map(this::convertToDictItemVO)
                .collect(Collectors.toList()));

        return dictVO;
    }

    private DictItemVO convertToDictItemVO(DictItem item) {
        DictItemVO itemVO = new DictItemVO();
        BeanUtils.copyProperties(item, itemVO);
        return itemVO;
    }

    private void sortDictItems(DictVO dictVO) {
        dictVO.setItems(
                dictVO.getItems().stream()
                        .sorted(Comparator.comparing(DictItemVO::getSorted))
                        .collect(Collectors.toList())
        );
    }
}