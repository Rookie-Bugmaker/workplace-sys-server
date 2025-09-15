package com.crrc.sys.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.redisson.RedissonManager;
import com.crrc.sys.entity.BaseEntity;
import com.crrc.sys.entity.iddesign.IdDesign;
import com.crrc.sys.entity.org.supplier.SysOrgSupplier;
import com.crrc.sys.mapper.IdDesignMapper;
import com.crrc.sys.mapper.SysOrgSupplierMapper;
import com.crrc.sys.model.iddesign.IdDesignListReqVO;
import com.crrc.sys.model.iddesign.IdDesignPageReqVO;
import com.crrc.sys.service.IdDesignService;
import com.crrc.sys.service.SysOrgSupplierService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 组织架构-供应商 服务实现类
 * </p>
 *
 * @author lijiangtao
 * @since 2025-07-11 11:55:04
 */
@Service
@Slf4j
public class SysOrgSupplierServiceImpl extends ServiceImpl<SysOrgSupplierMapper, SysOrgSupplier> implements SysOrgSupplierService {

}