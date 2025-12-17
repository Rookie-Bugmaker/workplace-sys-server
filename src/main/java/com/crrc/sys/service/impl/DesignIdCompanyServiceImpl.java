package com.crrc.sys.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.redisson.RedissonManager;
import com.crrc.sys.entity.BaseEntity;
import com.crrc.sys.entity.iddesign.DesignIdCompany;
import com.crrc.sys.entity.iddesign.IdDesign;
import com.crrc.sys.mapper.DesignIdCompanyMapper;
import com.crrc.sys.mapper.IdDesignMapper;
import com.crrc.sys.model.iddesign.IdDesignListReqVO;
import com.crrc.sys.model.iddesign.IdDesignPageReqVO;
import com.crrc.sys.service.DesignIdCompanyService;
import com.crrc.sys.service.IdDesignService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 自定义ID-公司隔离 服务实现类
 * </p>
 *
 * @author lijiangtao
 * @since 2025-12-16 09:55:04
 */
@Service
@Slf4j
public class DesignIdCompanyServiceImpl extends ServiceImpl<DesignIdCompanyMapper, DesignIdCompany> implements DesignIdCompanyService {


    @Override
    public Boolean deleteByDesignId(Long designId) {
        return this.remove(new LambdaUpdateWrapper<DesignIdCompany>().eq(DesignIdCompany::getDesignId, designId));
    }

    @Override
    public List<DesignIdCompany> listByDesignId(Long designId) {
        return this.list(new LambdaQueryWrapper<DesignIdCompany>().eq(DesignIdCompany::getDesignId, designId));
    }

    @Override
    public DesignIdCompany selectByDesignAndCompanyId(Long designId, Long companyId) {
        LambdaQueryWrapper<DesignIdCompany> wrapper = new LambdaQueryWrapper<DesignIdCompany>()
                .eq(DesignIdCompany::getDesignId, designId)
                .eq(DesignIdCompany::getCompanyId, companyId);
        return this.getOne(wrapper);
    }

    @Override
    public Boolean isExisted(Long designId) {
        LambdaQueryWrapper<DesignIdCompany> wrapper = new LambdaQueryWrapper<DesignIdCompany>()
                .eq(DesignIdCompany::getDesignId, designId);
        return this.count(wrapper) > 0;
    }

    @Override
    public Boolean isExisted(Long designId, Long companyId) {
        LambdaQueryWrapper<DesignIdCompany> wrapper = new LambdaQueryWrapper<DesignIdCompany>()
                .eq(DesignIdCompany::getDesignId, designId)
                .eq(DesignIdCompany::getCompanyId, companyId);
        return this.count(wrapper) > 0;
    }

    @Override
    public Boolean updateNumber(List<Long> idList, Integer newNumber) {
        LambdaUpdateWrapper<DesignIdCompany> wrapper = new LambdaUpdateWrapper<DesignIdCompany>()
                .in(DesignIdCompany::getId, idList)
                .set(DesignIdCompany::getSerialNumberCurrent, newNumber);

        return this.update(wrapper);
    }

    @Override
    public Boolean updateNumber(Long id, Integer newNumber) {
        LambdaUpdateWrapper<DesignIdCompany> wrapper = new LambdaUpdateWrapper<DesignIdCompany>()
                .eq(DesignIdCompany::getId, id)
                .set(DesignIdCompany::getSerialNumberCurrent, newNumber);

        return this.update(wrapper);
    }

    @Override
    public Boolean updateNumberPlusOne(Long designId, Long companyId) {
        DesignIdCompany designIdCompany = selectByDesignAndCompanyId(designId, companyId);
        if(designIdCompany != null) {
            LambdaUpdateWrapper<DesignIdCompany> wrapper = new LambdaUpdateWrapper<DesignIdCompany>()
                    .eq(DesignIdCompany::getDesignId, designId)
                    .eq(DesignIdCompany::getCompanyId, companyId)
                    .set(DesignIdCompany::getSerialNumberCurrent, designIdCompany.getSerialNumberCurrent() + 1);
            return this.update(wrapper);
        }
        return false;
    }

    @Override
    public DesignIdCompany create(Long designId, Long companyId, Integer newNumber) {
        DesignIdCompany designIdCompany = new DesignIdCompany(designId, companyId, newNumber);
        boolean saved = this.save(designIdCompany);
        return saved ? designIdCompany : null;
    }
}