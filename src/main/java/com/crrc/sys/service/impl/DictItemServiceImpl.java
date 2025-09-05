package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.BaseEntity;
import com.crrc.sys.entity.dict.Dict;
import com.crrc.sys.entity.dict.DictItem;
import com.crrc.sys.mapper.DictItemMapper;
import com.crrc.sys.model.dict.DictItemExcelVO;
import com.crrc.sys.model.dict.DictItemListReqVO;
import com.crrc.sys.model.dict.DictItemPageReqVO;
import com.crrc.sys.model.dict.DictPageReqVO;
import com.crrc.sys.service.DictItemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 字典项 服务实现类
 * </p>
 *
 * @author lijiangtao
 * @since 2025-01-10
 */
@Service
@Slf4j
public class DictItemServiceImpl extends ServiceImpl<DictItemMapper, DictItem> implements DictItemService {
    @Autowired
    DictServiceImpl dictService;

    @Override
    public Boolean saveDictItemExcelVO(List<DictItemExcelVO> list) {
        // 1. 参数校验
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }

        // 2. 获取所有字典编码并映射为字典ID
        List<String> dictCodes = list.stream()
                .map(DictItemExcelVO::getDictCode)
                .distinct()
                .collect(Collectors.toList());
        Map<String, Long> dictMap = getCodeToIdDictMap(dictCodes);

        // 3. 按字典ID分组检查重复项
        Map<Long, List<DictItemExcelVO>> groupedByDictId = list.stream()
                .collect(Collectors.groupingBy(
                        vo -> dictMap.get(vo.getDictCode()),
                        Collectors.toList()
                ));

        // 4. 检查每个字典ID下的字典项编码是否重复
        for (Map.Entry<Long, List<DictItemExcelVO>> entry : groupedByDictId.entrySet()) {
            Long dictId = entry.getKey();
            List<DictItemExcelVO> items = entry.getValue();

            // 检查上传数据中是否有重复的itemCode
            Set<String> itemCodes = new HashSet<>();
            for (DictItemExcelVO item : items) {
                if (!itemCodes.add(item.getItemCode())) {
                    // 发现重复项
                    return false;
                }
            }

            // 检查数据库中是否已存在相同的itemCode
            List<String> existingCodes = this.list(new LambdaQueryWrapper<DictItem>()
                            .select(DictItem::getItemCode)
                            .eq(DictItem::getDictId, dictId))
                    .stream()
                    .map(DictItem::getItemCode)
                    .collect(Collectors.toList());

            if (items.stream().anyMatch(item -> existingCodes.contains(item.getItemCode()))) {
                return false;
            }
        }

        // 5. 转换并批量保存
        List<DictItem> dictItemList = list.stream()
                .map(vo -> {
                    DictItem item = BeanUtils.toBean(vo, DictItem.class);
                    item.setDictId(dictMap.get(vo.getDictCode()));
                    return item;
                })
                .collect(Collectors.toList());

        return this.saveBatch(dictItemList);
    }

    @Override
    public List<DictItemExcelVO> getDictItemExcelVOs(List<Dict> list) {
        List<DictItemExcelVO> dictItemExcelVOList = new ArrayList<>();
        Map<Long, String> dictMap = getIdToCodeDictMap(list);
        List<Long> idList = list.stream().map(Dict::getId).collect(Collectors.toList());
        LambdaQueryWrapper<DictItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DictItem::getDictId, idList);
        this.list(queryWrapper).stream().forEach(data -> {
            DictItemExcelVO dictItemExcelVO = BeanUtils.toBean(data, DictItemExcelVO.class);
            dictItemExcelVO.setDictCode(dictMap.get(data.getDictId()));
            dictItemExcelVOList.add(dictItemExcelVO);
        });
        return dictItemExcelVOList;
    }

    private Map<String, Long> getCodeToIdDictMap(List<String> dictCodes) {
        Set<String> collect = dictCodes.stream().collect(Collectors.toSet());
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Dict::getId, Dict::getDictCode)
                .in(Dict::getDictCode, dictCodes);
        List<Dict> list = dictService.list(queryWrapper);
        if(list.size() != collect.size()) {
            throw new BizException("部分字典编码不存在");
        }
        return list.stream().collect(Collectors.toMap(Dict::getDictCode, Dict::getId));
    }

    private Map<Long,String> getIdToCodeDictMap(List<Dict> list) {
        return list.stream().collect(Collectors.toMap(Dict::getId, Dict::getDictCode));
    }

    @Override
    public List<DictItem> getItemByMainId(Long dictId) {
        if (dictId == null) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<DictItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictItem::getDictId, dictId);
        queryWrapper.orderByAsc(DictItem::getSorted);

        try {
            return list(queryWrapper);
        } catch (Exception e) {
            log.error("根据字典ID查询字典项异常，dictId: {}", dictId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public DictItem getItemByItemId(Long dictId, String itemCode) {
        if (dictId == null || StringUtils.isBlank(itemCode)) {
            return null;
        }

        LambdaQueryWrapper<DictItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictItem::getDictId, dictId)
                .eq(DictItem::getItemCode, itemCode);

        List<DictItem> dictItemList = list(queryWrapper);
        try {
            return dictItemList != null && dictItemList.size() > 0 ? dictItemList.get(0) : null;
        } catch (Exception e) {
            log.error("根据字典ID和项编码查询字典项异常，dictId: {}, itemCode: {}", dictId, itemCode, e);
            return null;
        }
    }

    @Override
    public DictItem getItemByItemName(Long dictId, String itemName) {
        if (dictId == null || StringUtils.isBlank(itemName)) {
            return null;
        }

        LambdaQueryWrapper<DictItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictItem::getDictId, dictId)
                .eq(DictItem::getItemName, itemName);

        List<DictItem> dictItemList = list(queryWrapper);
        try {
            return dictItemList != null && dictItemList.size() > 0 ? dictItemList.get(0) : null;
        } catch (Exception e) {
            log.error("根据字典ID和项名称查询字典项异常，dictId: {}, itemName: {}", dictId, itemName, e);
            return null;
        }
    }

    @Override
    public PaginationGrid<DictItem> page(DictItemPageReqVO pageReqVO) {
        pageReqVO = (pageReqVO == null) ? new DictItemPageReqVO() : pageReqVO;
        IPage<DictItem> page = new Page<>(pageReqVO.getPage(), pageReqVO.getPageSize());
        LambdaQueryWrapper<DictItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(pageReqVO.getItemCode()), DictItem::getItemCode, pageReqVO.getItemCode());
        queryWrapper.like(StringUtils.isNotBlank(pageReqVO.getItemName()), DictItem::getItemName, pageReqVO.getItemName());
        queryWrapper.eq(pageReqVO.getDictId() != null, DictItem::getDictId, pageReqVO.getDictId());
        queryWrapper.orderByAsc(DictItem::getSorted);
        page = this.page(page, queryWrapper);
        return PaginationGrid.success(page);
    }

    @Override
    public List<DictItem> query(DictItemListReqVO queryReqVO) {
        queryReqVO = (queryReqVO == null) ? new DictItemListReqVO() : queryReqVO;
        LambdaQueryWrapper<DictItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(queryReqVO.getItemCode()), DictItem::getItemCode, queryReqVO.getItemCode());
        queryWrapper.like(StringUtils.isNotBlank(queryReqVO.getItemName()), DictItem::getItemName, queryReqVO.getItemName());
        queryWrapper.eq(StringUtils.isNotBlank(queryReqVO.getSorted()), DictItem::getSorted, queryReqVO.getSorted());
        queryWrapper.eq(queryReqVO.getDictId() != null, DictItem::getDictId, queryReqVO.getDictId());
        queryWrapper.orderByAsc(DictItem::getSorted);
        return this.list(queryWrapper);
    }
}
