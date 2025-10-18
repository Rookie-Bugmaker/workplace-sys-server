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
import com.crrc.sys.mapper.IdDesignMapper;
import com.crrc.sys.model.iddesign.IdDesignListReqVO;
import com.crrc.sys.model.iddesign.IdDesignPageReqVO;
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
 * 自定义ID 服务实现类
 * </p>
 *
 * @author lijiangtao
 * @since 2025-07-11 11:55:04
 */
@EnableScheduling
@Service
@Slf4j
public class IdDesignServiceImpl extends ServiceImpl<IdDesignMapper, IdDesign> implements IdDesignService {

    private static final long TIME_BUFFER_MILLIS = 1000; // 1秒时间缓冲窗口

    @Override
    public PaginationGrid<IdDesign> page(IdDesignPageReqVO pageReqVO) {
        pageReqVO = (pageReqVO == null) ? new IdDesignPageReqVO() : pageReqVO;
        IPage<IdDesign> page = new Page<>(pageReqVO.getPage(), pageReqVO.getPageSize());
        LambdaQueryWrapper<IdDesign> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(pageReqVO.getId() != null, IdDesign::getId, pageReqVO.getId());
        queryWrapper.like(StringUtils.isNotBlank(pageReqVO.getBusinessIdentifier()), IdDesign::getBusinessIdentifier, pageReqVO.getBusinessIdentifier());
        queryWrapper.eq(StringUtils.isNotBlank(pageReqVO.getUniqueCodeType()), IdDesign::getUniqueCodeType, pageReqVO.getUniqueCodeType());
        queryWrapper.orderByDesc(BaseEntity::getUpdateTime);
        page = this.page(page, queryWrapper);
        return PaginationGrid.success(page);
    }

    @Override
    public List<IdDesign> query(IdDesignListReqVO queryReqVO) {
        queryReqVO = (queryReqVO == null) ? new IdDesignListReqVO() : queryReqVO;
        LambdaQueryWrapper<IdDesign> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(queryReqVO.getId() != null, IdDesign::getId, queryReqVO.getId());
        queryWrapper.like(StringUtils.isNotBlank(queryReqVO.getBusinessIdentifier()), IdDesign::getBusinessIdentifier, queryReqVO.getBusinessIdentifier());
        queryWrapper.eq(StringUtils.isNotBlank(queryReqVO.getUniqueCodeType()), IdDesign::getUniqueCodeType, queryReqVO.getUniqueCodeType());
        queryWrapper.orderByDesc(BaseEntity::getUpdateTime);
        return this.list(queryWrapper);
    }

    @Override
    public String nextId(String businessId, Map<Integer, String> positionToParamMap) {
        // 查询自定义ID配置
        LambdaQueryWrapper<IdDesign> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IdDesign::getBusinessIdentifier, businessId);
        IdDesign idDesign = this.getOne(queryWrapper);
        if (idDesign == null) {
            throw new BizException("业务标识" + businessId + "不存在");
        }

        // 创建 concatMap，用于存储各部分内容
        Map<Integer, String> concatMap = new TreeMap<>();
        // 如果动态参数不为空，加入配置中
        if(positionToParamMap != null && positionToParamMap.size() > 0){
            concatMap.putAll(positionToParamMap);
        }

        // 1. 处理唯一编码类型
        String uniqueCode;
        switch (idDesign.getUniqueCodeType()) {
            case "0": // UUID
                uniqueCode = IdUtil.randomUUID();
                break;
            case "1": // 雪花ID
                uniqueCode = IdUtil.getSnowflakeNextIdStr();
                break;
            case "2": // 流水号
                int currentValue = idDesign.getSerialNumberCurrent();
                int digits = idDesign.getSerialNumberDigits();
                String serialNumber = String.valueOf(currentValue);
                if (serialNumber.length() > digits) {
                    throw new BizException("流水号位数不足");
                }
                uniqueCode = StrUtil.padPre(serialNumber, digits, '0'); // 左侧补零
                break;
            default:
                throw new BizException("未知的唯一编码类型");
        }
        concatMap.put(Integer.parseInt(idDesign.getUniqueCodePosition()), uniqueCode);

        // 2. 处理时间
        if ("0".equals(idDesign.getEnableTime())) {
            String timeFormat = idDesign.getTimeFormat();
            String currentTime = DateUtil.format(DateUtil.date(), timeFormat);
            concatMap.put(Integer.parseInt(idDesign.getTimePosition()), currentTime);
        }

        // 3. 处理自定义字符串
        if ("0".equals(idDesign.getEnableString())) {
            String[] customStrings = idDesign.getCustomString().split(";");
            String[] stringPositions = idDesign.getStringPosition().split(";");
            for (int i = 0; i < customStrings.length; i++) {
                concatMap.put(Integer.parseInt(stringPositions[i]), customStrings[i]);
            }
        }

        // 4. 拼接生成ID
        String spliceSeparator = idDesign.getSpliceSeparator() == null ? "" : idDesign.getSpliceSeparator();
        StringBuilder idBuilder = new StringBuilder();
        for (Map.Entry<Integer, String> entry : concatMap.entrySet()) {
            idBuilder.append(entry.getValue()).append(spliceSeparator);
        }
        String id = idBuilder.toString();
        if (StrUtil.isNotBlank(spliceSeparator) && id.endsWith(spliceSeparator)) {
            id = id.substring(0, id.length() - 1); // 去掉最后一个分隔符
        }

        // 5. 更新流水号当前值
        if ("2".equals(idDesign.getUniqueCodeType())) {
            idDesign.setSerialNumberCurrent(idDesign.getSerialNumberCurrent() + 1);
            this.updateById(idDesign);
        }

        return id;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public String safeNextId(String businessId, Map<Integer, String> positionToParamMap) {
        log.info("开始生成ID，业务标识: {}", businessId);
        String lockKey = "id_generation_lock_" + businessId;
        RLock lock = RedissonManager.getRedissonClient().getFairLock(lockKey); // 使用公平锁

        int maxRetries = 3;
        int retryCount = 0;
        long baseWaitTime = 100; // 基础等待时间（毫秒）

        while (retryCount < maxRetries) {
            try {
                // 动态调整锁的超时时间
                boolean isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);
                if (!isLocked) {
                    retryCount++;
                    if (retryCount >= maxRetries) {
                        log.warn("获取锁失败，业务标识: {}", businessId);
                        throw new BizException("当前并发较高，获取锁失败，请稍后重试");
                    }
                    // 指数退避重试
                    Thread.sleep(baseWaitTime * (1 << retryCount));
                    continue;
                }

                // 生成ID的逻辑
                String id = this.nextId(businessId, positionToParamMap);
                log.info("成功生成ID: {}, 业务标识: {}", id, businessId);
                return id;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("获取锁时被中断，业务标识: {}", businessId, e);
                throw new BizException("获取锁时被中断: " + e.getMessage());
            } catch (Exception e) {
                log.error("生成ID时发生异常，业务标识: {}", businessId, e);
                throw new BizException("生成ID失败，请稍后重试: " + e.getMessage());
            } finally {
                // 释放锁，确保当前线程持有锁
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    log.info("释放锁，业务标识: {}", businessId);
                }
            }
        }
        throw new BizException("重试次数耗尽，生成ID失败");
    }

    // 新增定时任务方法
    // 每天 0 0 0 * * *
    // 每小时 0 0 * * * *
    // 每分钟 0 * * * * *
    // 每秒钟 * * * * * *
    @Scheduled(cron = "0 0 * * * *") // 每小时执行一次
    public void scheduledResetSerialNumbers() {
        RLock lock = RedissonManager.getRedissonClient().getLock("serial_number_reset_lock");
        try {
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                log.info("开始执行流水号周期重置检查...");
                resetAllCycleSerialNumbers();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("定时任务获取锁中断", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private Boolean cycleDateOver(IdDesign idDesign) {
        if (idDesign.getLastResetDate() == null || idDesign.getCycleUnit() == null || idDesign.getCycleDate() == null) {
            return true;
        }
        // 精确到纳秒
        Instant now = Instant.now();
        Instant lastResetInstant = idDesign.getLastResetDate().atZone(ZoneId.systemDefault()).toInstant();

        // 计算精确的下次重置时间点
        Instant nextResetTime = calculateNextResetTime(lastResetInstant, idDesign);

        // 添加时间缓冲窗口：当前时间 >= (下次重置时间 - 缓冲窗口)
        return !now.isBefore(nextResetTime.minusMillis(TIME_BUFFER_MILLIS));
    }

    // 计算精确的下次重置时间
    private Instant calculateNextResetTime(Instant lastReset, IdDesign idDesign) {
        switch (idDesign.getCycleUnit()) {
            case "HOUR":
                return lastReset.plus(idDesign.getCycleDate(), ChronoUnit.HOURS);
            case "DAY":
                return lastReset.plus(idDesign.getCycleDate(), ChronoUnit.DAYS);
            case "WEEK":
                return lastReset.plus(idDesign.getCycleDate() * 7L, ChronoUnit.DAYS);
            case "MONTH":
                // 转换为ZonedDateTime处理月份加减
                return lastReset.atZone(ZoneId.systemDefault())
                        .plusMonths(idDesign.getCycleDate())
                        .toInstant();
            case "YEAR":
                // 转换为ZonedDateTime处理年份加减
                return lastReset.atZone(ZoneId.systemDefault())
                        .plusYears(idDesign.getCycleDate())
                        .toInstant();
            default:
                throw new BizException("未知的循环单位: " + idDesign.getCycleUnit());
        }
    }

    @Transactional
    void resetSerialNumberCurrent(IdDesign idDesign) {
        // 重置流水号为起始值
        idDesign.setSerialNumberCurrent(idDesign.getSerialNumberStart());
        // 更新最后重置时间为当前时间
        idDesign.setLastResetDate(LocalDateTime.now());
        // 更新数据库记录
        this.updateById(idDesign);
        log.info("重置流水号成功，业务标识: {}, 当前值: {}",
                idDesign.getBusinessIdentifier(),
                idDesign.getSerialNumberCurrent());
    }

    // 批量重置方法
    private void resetAllCycleSerialNumbers() {
        int pageSize = 100;
        int currentPage = 1;
        boolean hasMore = true;

        while (hasMore) {
            Page<IdDesign> page = new Page<>(currentPage, pageSize);
            LambdaQueryWrapper<IdDesign> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(IdDesign::getUniqueCodeType, "2")
                    .eq(IdDesign::getEnableCycle, "0")
                    .isNotNull(IdDesign::getCycleUnit)
                    .isNotNull(IdDesign::getCycleDate);

            IPage<IdDesign> result = this.page(page, queryWrapper);
            List<IdDesign> records = result.getRecords();

            for (IdDesign config : records) {
                if (cycleDateOver(config)) {
                    resetSerialNumberCurrent(config);
                    log.info("定时重置流水号: {} => {}",
                            config.getBusinessIdentifier(),
                            config.getSerialNumberStart());
                }
            }

            hasMore = currentPage * pageSize < result.getTotal();
            currentPage++;
        }
    }
}