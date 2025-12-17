package com.crrc.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.iddesign.DesignIdCompany;
import com.crrc.sys.entity.iddesign.IdDesign;
import com.crrc.sys.model.iddesign.IdDesignListReqVO;
import com.crrc.sys.model.iddesign.IdDesignPageReqVO;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * 自定义ID-公司隔离 服务类
 * </p>
 *
 * @author lijiangtao
 * @since 2025-12-16 09:55:04
 */
public interface DesignIdCompanyService extends IService<DesignIdCompany> {
    Boolean deleteByDesignId(Long designId);

    List<DesignIdCompany> listByDesignId(Long designId);

    DesignIdCompany selectByDesignAndCompanyId(Long designId, Long companyId);

    Boolean isExisted(Long designId);

    Boolean isExisted(Long designId, Long companyId);

    Boolean updateNumber(List<Long> idList, Integer newNumber);

    Boolean updateNumber(Long id, Integer newNumber);

    Boolean updateNumberPlusOne(Long designId, Long companyId);

    DesignIdCompany create(Long designId, Long companyId, Integer newNumber);
}