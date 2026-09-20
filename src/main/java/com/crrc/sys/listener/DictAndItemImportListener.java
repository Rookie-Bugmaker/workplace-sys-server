package com.crrc.sys.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.dict.Dict;
import com.crrc.sys.entity.dict.DictItem;
import com.crrc.sys.model.dict.DictAndItemExcelVO;
import com.crrc.sys.model.dict.DictItemExcelVO;
import com.crrc.sys.model.dict.DictItemVO;
import com.crrc.sys.model.dict.DictVO;
import com.crrc.sys.service.DictCacheService;
import com.crrc.sys.service.DictItemService;
import com.crrc.sys.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class DictAndItemImportListener extends AnalysisEventListener<DictAndItemExcelVO> {
    private final DictService dictService;
    private final DictItemService dictItemService;
    private final DictCacheService dictCacheService;
    private final Set<String> allowedProjectCodes;
    private final boolean projectAuthEnabled;

    private final Map<String, DictVO> dictCodeToDictMap = new HashMap<>();
    private final Map<String, Set<String>> dictItemCodeMap = new HashMap<>();

    private final Map<Long, String> dictCodeIdToCodeMap = new HashMap<>();
    private final List<String> errorMessages = new ArrayList<>();

    public DictAndItemImportListener(DictService dictService,
                                   DictItemService dictItemService,
                                   DictCacheService dictCacheService) {
        this(dictService, dictItemService, dictCacheService, null);
    }

    /**
     * @param allowedProjectCodes 当前用户可访问的项目编码集合；
     *                             非空时启用项目数据权限校验，导入非公共项目字典需在集合内；
     *                             传 null 表示不启用项目权限校验。
     */
    public DictAndItemImportListener(DictService dictService,
                                   DictItemService dictItemService,
                                   DictCacheService dictCacheService,
                                   Set<String> allowedProjectCodes) {
        this.dictService = dictService;
        this.dictItemService = dictItemService;
        this.dictCacheService = dictCacheService;
        this.allowedProjectCodes = allowedProjectCodes;
        this.projectAuthEnabled = allowedProjectCodes != null;
    }

    @Override
    public void invoke(DictAndItemExcelVO data, AnalysisContext context) {
        int row = context.readRowHolder().getRowIndex() + 1;

        // 基本校验
        if (StringUtils.isBlank(data.getDictCode())) {
            errorMessages.add("第" + row + "行: 字典编码不能为空");
            return;
        }

        if (StringUtils.isBlank(data.getDictName())) {
            errorMessages.add("第" + row + "行: 字典名称不能为空");
            return;
        }
        // 项目数据权限校验：公共字典放行，项目字典需在 allowedProjectCodes 内
        if (projectAuthEnabled && StringUtils.isNotBlank(data.getProjectCode())
                && !allowedProjectCodes.contains(data.getProjectCode())) {
            errorMessages.add("第" + row + "行: 无该项目的数据权限：" + data.getProjectCode());
            return;
        }
        DictVO dict;
        // 检查字典编码是否已经处理过
        if (!dictCodeToDictMap.containsKey(data.getDictCode())) {
            dict = new DictVO();
            // 处理字典
            dict.setDictCode(data.getDictCode());
            dict.setDictName(data.getDictName());
            dict.setDictType(data.getDictType());
            dict.setRemark(data.getDictRemark());
            dict.setProjectCode(data.getProjectCode());
            // 处理字典项
            processDictItem(dict, data, row);
            dictCodeToDictMap.put(data.getDictCode(), dict);
        } else {
            dict = dictCodeToDictMap.get(data.getDictCode());
            // 处理字典项
            processDictItem(dict, data, row);
        }
    }

    private void processDictItem(DictVO dict, DictAndItemExcelVO data, int row) {
        if (StringUtils.isBlank(data.getItemCode())) {
            return; // 允许字典行没有字典项
        }
        
        if (StringUtils.isBlank(data.getItemName())) {
            errorMessages.add("第" + row + "行: 字典项名称不能为空");
            return;
        }
        
        // 检查字典项编码是否重复
        Set<String> itemCodes = dictItemCodeMap.computeIfAbsent(data.getDictCode(), k -> new HashSet<>());
        if (!itemCodes.add(data.getItemCode())) {
            errorMessages.add("第" + row + "行: 字典" + data.getDictCode() + "下的字典项编码" + data.getItemCode() + "重复");
            return;
        }
        
        DictItemVO dictItem = new DictItemVO();
        dictItem.setItemCode(data.getItemCode());
        dictItem.setItemName(data.getItemName());
        dictItem.setSorted(StringUtils.isNotBlank(data.getItemSorted()) ? data.getItemSorted() : "0");
        dict.getItems().add(dictItem);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!errorMessages.isEmpty()) {
            throw new BizException(String.join("; ", errorMessages));
        }
        List<Dict> dictList = new ArrayList<>(); //新增队列
        Map<String,List<DictItemVO>> dictItemVOInNew = new HashMap<>(); // 不存在的字典项
        List<DictItem> dictItemList = new ArrayList<>();
        // 保存字典数据
        if (!dictCodeToDictMap.isEmpty()) {
            dictCodeToDictMap.entrySet().forEach(kv -> {
                String dictCode = kv.getKey();
                DictVO dictVO = kv.getValue();
                Dict dictInDB = dictService.getByCode(dictCode);
                // 如果字典不存在，加入新增队列并记录对应的字典项值
                if(dictInDB == null) {
                    Dict dict = BeanUtils.toBean(dictVO, Dict.class);
                    dictList.add(dict);
                    dictItemVOInNew.put(dictVO.getDictCode(), dictVO.getItems());
                } else {
                    // 如果字典存在，需要判断字典项的更新
                    Long dictId = dictInDB.getId();
                    dictVO.getItems().forEach(itemVO -> {
                        DictItem dictItemInDB = dictItemService.getItemByItemId(dictId, itemVO.getItemCode());
                        // 如果字典项不存在，进行处理
                        if(dictItemInDB == null) {
                            DictItem dictItem = BeanUtils.toBean(itemVO, DictItem.class);
                            dictItem.setDictId(dictId);
                            dictItemList.add(dictItem);
                            dictCodeIdToCodeMap.put(dictId, dictCode);
                        }else {
                            throw new BizException("字典编码为"+dictCode+"的字典项"+itemVO.getItemCode()+"已经存在");
                        }
                    });
                }
            });
            dictService.saveBatch(dictList);
            dictList.forEach(dict -> {
                // 更新缓存
                dictCacheService.setDictCache(dict);
                // 加入新增字典项
                Long dictId = dict.getId();
                String dictCode = dict.getDictCode();
                for (DictItemVO itemVO : dictItemVOInNew.get(dictCode)) {
                    DictItem dictItem = BeanUtils.toBean(itemVO, DictItem.class);
                    dictItem.setDictId(dictId);
                    dictItemList.add(dictItem);
                    dictCodeIdToCodeMap.put(dictId, dictCode);
                }
            });
        }
        
        // 保存字典项数据
        if (!dictItemList.isEmpty()) {
            dictItemService.saveBatch(dictItemList);
            
            // 更新缓存
            dictCacheService.batchSetDictItemCache(dictItemList.stream()
                    .map(item -> {
                        DictItemExcelVO vo = new DictItemExcelVO();
                        vo.setDictCode(dictCodeIdToCodeMap.get(item.getDictId()));
                        vo.setItemCode(item.getItemCode());
                        vo.setItemName(item.getItemName());
                        vo.setSorted(String.valueOf(item.getSorted()));
                        return vo;
                    })
                    .collect(Collectors.toList()));
        }
    }
}