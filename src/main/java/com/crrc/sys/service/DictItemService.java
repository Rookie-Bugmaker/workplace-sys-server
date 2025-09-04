package com.crrc.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.dict.Dict;
import com.crrc.sys.entity.dict.DictItem;
import com.crrc.sys.model.dict.*;

import java.util.List;

/**
 * <p>
 * 字典项 服务类
 * </p>
 *
 * @author lijiangtao
 * @since 2025-01-10
 */
public interface DictItemService extends IService<DictItem> {

    PaginationGrid<DictItem> page(DictItemPageReqVO pageReqVO);

    List<DictItem> query(DictItemListReqVO queryReqVO);
    public Boolean saveDictItemExcelVO(List<DictItemExcelVO> list);

    public List<DictItemExcelVO> getDictItemExcelVOs(List<Dict> list);
    List<DictItem> getItemByMainId(Long dictId);
    DictItem getItemByItemId(Long dictId, String itemCode);
    DictItem getItemByItemName(Long dictId, String itemName);
}
