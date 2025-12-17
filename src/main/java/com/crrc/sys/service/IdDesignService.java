package com.crrc.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.dict.DictItem;
import com.crrc.sys.entity.iddesign.IdDesign;
import com.crrc.sys.model.dict.DictItemListReqVO;
import com.crrc.sys.model.dict.DictItemPageReqVO;
import com.crrc.sys.model.iddesign.IdDesignListReqVO;
import com.crrc.sys.model.iddesign.IdDesignPageReqVO;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * 自定义ID 服务类
 * </p>
 *
 * @author lijiangtao
 * @since 2025-07-11 11:55:04
 */
public interface IdDesignService extends IService<IdDesign> {

    PaginationGrid<IdDesign> page(IdDesignPageReqVO pageReqVO);

    List<IdDesign> query(IdDesignListReqVO queryReqVO);

    List<IdDesign> queryExcel(IdDesignListReqVO queryReqVO);

    String nextId(String businessId, Map<Integer, String> positionToParamMap, Long companyId);

    String safeNextId(String businessId, Map<Integer, String> positionToParamMap, Long companyId);
}