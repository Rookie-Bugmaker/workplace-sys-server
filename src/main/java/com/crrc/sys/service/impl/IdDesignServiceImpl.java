package com.crrc.sys.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.redisson.RedissonManager;
import com.crrc.sys.entity.BaseEntity;
import com.crrc.sys.entity.iddesign.DesignIdCompany;
import com.crrc.sys.entity.iddesign.IdDesign;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.enums.IdDesignEnableEnum;
import com.crrc.sys.enums.IdDesignUniqueTypeEnum;
import com.crrc.sys.mapper.IdDesignMapper;
import com.crrc.sys.model.iddesign.IdDesignListReqVO;
import com.crrc.sys.model.iddesign.IdDesignPageReqVO;
import com.crrc.sys.service.IdDesignService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Autowired
    private DesignIdCompanyServiceImpl designIdCompanyService;

    @Resource
    private AuthClient authClient;

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
    public List<IdDesign> queryExcel(IdDesignListReqVO queryReqVO) {
        queryReqVO = (queryReqVO == null) ? new IdDesignListReqVO() : queryReqVO;
        LambdaQueryWrapper<IdDesign> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(IdDesign::getBusinessIdentifier, IdDesign::getTimeFormat, IdDesign::getTimePosition,
                IdDesign::getEnableTime, IdDesign::getUniqueCodeType, IdDesign::getSerialNumberStart,
                IdDesign::getSerialNumberCurrent, IdDesign::getSerialNumberDigits, IdDesign::getUniqueCodePosition,
                IdDesign::getCustomString, IdDesign::getStringPosition, IdDesign::getEnableString,
                IdDesign::getSpliceSeparator, IdDesign::getEnableCycle, IdDesign::getCycleUnit, IdDesign::getCycleDate,
                IdDesign::getLastResetDate, IdDesign::getEnableCompany);
        queryWrapper.eq(queryReqVO.getId() != null, IdDesign::getId, queryReqVO.getId());
        queryWrapper.like(StringUtils.isNotBlank(queryReqVO.getBusinessIdentifier()), IdDesign::getBusinessIdentifier, queryReqVO.getBusinessIdentifier());
        queryWrapper.eq(StringUtils.isNotBlank(queryReqVO.getUniqueCodeType()), IdDesign::getUniqueCodeType, queryReqVO.getUniqueCodeType());
        queryWrapper.orderByDesc(BaseEntity::getUpdateTime);
        return this.list(queryWrapper);
    }

    @Override
    public String nextId(String businessId, Map<Integer, String> positionToParamMap, Long companyId) {
        // 查询自定义ID配置
        LambdaQueryWrapper<IdDesign> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IdDesign::getBusinessIdentifier, businessId);
        IdDesign idDesign = this.getOne(queryWrapper);
        if (idDesign == null) {
            throw new BizException("流水号生成错误-业务标识" + businessId + "不存在");
        }

        // 创建 concatMap，用于存储各部分内容
        Map<Integer, String> concatMap = new TreeMap<>();
        // 如果动态参数不为空，加入配置中
        if(positionToParamMap != null && positionToParamMap.size() > 0){
            concatMap.putAll(positionToParamMap);
        }
        // 如果开启了数据隔离，需要获取当前用户或设置给定的公司
        Long currentCompanyId = null;
        if(IdDesignEnableEnum.TRUE.getValue().equals(idDesign.getEnableCompany())) {
            if(companyId != null) {
                currentCompanyId = companyId;
            } else {
                ResultData<SysUser> resultData = authClient.currentUser();
                if (!resultData.getSuccess()) {
                    throw new BizException("流水号生成错误-获取当前用户失败，具体错误为：" + resultData.getMessage());
                }
                SysUser currentUser = resultData.getResult();
                if(currentUser == null) {
                    throw new BizException("流水号生成错误-流水号"+idDesign.getBusinessIdentifier()+"开启公司隔离，获取当前用户失败");
                }
                currentCompanyId = currentUser.getCurrentCompanyId();
            }
        }
        // 1. 处理唯一编码类型
        String uniqueCode;
        if(IdDesignUniqueTypeEnum.UUID.getValue().equals(idDesign.getUniqueCodeType())){
            uniqueCode = IdUtil.randomUUID();
        } else if (IdDesignUniqueTypeEnum.SNOWFLAKE.getValue().equals(idDesign.getUniqueCodeType())) {
            uniqueCode = IdUtil.getSnowflakeNextIdStr();
        } else if (IdDesignUniqueTypeEnum.SERIAL.getValue().equals(idDesign.getUniqueCodeType())) {
            uniqueCode = getCurrentSerialNumber(idDesign, currentCompanyId);
        } else {
            throw new BizException("流水号生成错误-未知的唯一编码类型");
        }
        concatMap.put(Integer.parseInt(idDesign.getUniqueCodePosition()), uniqueCode);

        // 2. 处理时间
        if (IdDesignEnableEnum.TRUE.getValue().equals(idDesign.getEnableTime())) {
            String timeFormat = idDesign.getTimeFormat();
            String currentTime = DateUtil.format(DateUtil.date(), timeFormat);
            concatMap.put(Integer.parseInt(idDesign.getTimePosition()), currentTime);
        }

        // 3. 处理自定义字符串
        if (IdDesignEnableEnum.TRUE.getValue().equals(idDesign.getEnableString())) {
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
        if (IdDesignUniqueTypeEnum.SERIAL.getValue().equals(idDesign.getUniqueCodeType())) {
            updateCurrentSerialNumber(idDesign, currentCompanyId);
        }

        return id;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public String safeNextId(String businessId, Map<Integer, String> positionToParamMap, Long companyId) {
        log.info("流水号生成初始-开始生成ID，业务标识: {}", businessId);
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
                        log.warn("流水号警告-获取锁失败，业务标识: {}", businessId);
                        throw new BizException("流水号生成错误-当前并发较高，获取锁失败，请稍后重试");
                    }
                    // 指数退避重试
                    Thread.sleep(baseWaitTime * (1 << retryCount));
                    continue;
                }

                // 生成ID的逻辑
                String id = this.nextId(businessId, positionToParamMap, companyId);
                log.info("流水号获取成功-成功生成ID: {}, 业务标识: {}", id, businessId);
                return id;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("流水号生成错误-获取锁时被中断，业务标识: {}", businessId, e);
                throw new BizException("获取锁时被中断: " + e.getMessage());
            } catch (BizException e) {
                throw e;
            }  catch (Exception e) {
                log.error("流水号生成错误-生成ID时发生异常，业务标识: {}", businessId, e);
                throw new BizException("流水号生成错误-生成ID失败，请稍后重试: " + e.getMessage());
            } finally {
                // 释放锁，确保当前线程持有锁
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    log.info("流水号生成最终处理-释放锁，业务标识: {}", businessId);
                }
            }
        }
        throw new BizException("流水号生成错误-重试次数耗尽，生成ID失败");
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
            log.error("流水号生成错误-定时任务获取锁中断", e);
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
                throw new BizException("流水号生成错误-未知的循环单位: " + idDesign.getCycleUnit());
        }
    }

    @Transactional
    void resetSerialNumberCurrent(IdDesign idDesign, Integer newCurrentNumber, LocalDateTime lastResetDate) {
        // 需要重置两种情况，默认流水号和公司隔离流水号
        Long designId = idDesign.getId();
        // 更新数据库记录
        LambdaUpdateWrapper<IdDesign> wrapper = new LambdaUpdateWrapper<>();
        // 重置流水号为起始值
        wrapper.set(IdDesign::getSerialNumberCurrent, newCurrentNumber);
        // 更新最后重置时间为当前时间
        wrapper.set(IdDesign::getLastResetDate, lastResetDate);
        wrapper.eq(IdDesign::getId, designId);
        this.update(wrapper);

        // 存在公司隔离流水号情况下更新
        if(designIdCompanyService.isExisted(designId)) {
            List<Long> idList = designIdCompanyService.listByDesignId(designId).
                    stream()
                    .map(DesignIdCompany::getId)
                    .collect(Collectors.toList());
            designIdCompanyService.updateNumber(idList, newCurrentNumber);
        }
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
                    Integer newCurrentNumber = config.getSerialNumberStart();
                    LocalDateTime lastResetDate = LocalDateTime.now();
                    resetSerialNumberCurrent(config, newCurrentNumber, lastResetDate);
                    log.info("定时重置流水号，业务标识: {}, 当前值: {}",
                            config.getBusinessIdentifier(), newCurrentNumber);
                }
            }

            hasMore = currentPage * pageSize < result.getTotal();
            currentPage++;
        }
    }

    // 获取当前流水号
    private String getCurrentSerialNumber(IdDesign idDesign, Long currentCompanyId) {
        int currentValue;
        // 当开启公司隔离，需要根据当前用户查找对应公司流水号
        if(IdDesignEnableEnum.TRUE.getValue().equals(idDesign.getEnableCompany())) {
            DesignIdCompany designIdCompany = getCompanyIsolationByCurrentUser(idDesign, currentCompanyId);
            if(designIdCompany != null) {
                currentValue = designIdCompany.getSerialNumberCurrent();
            } else {
                throw new BizException("流水号生成错误-流水号"+idDesign.getBusinessIdentifier()+"开启公司隔离，获取ID为" +
                        currentCompanyId + "的数据失败");
            }
        }else {
            currentValue = idDesign.getSerialNumberCurrent();
        }
        int digits = idDesign.getSerialNumberDigits();
        String serialNumber = String.valueOf(currentValue);
        if (serialNumber.length() > digits) {
            throw new BizException("流水号生成错误-流水号位数不足");
        }
        return StrUtil.padPre(serialNumber, digits, '0'); // 左侧补零
    }

    // 更新当前流水号
    private void updateCurrentSerialNumber(IdDesign idDesign, Long currentCompanyId) {
        // 当开启公司隔离，需要根据当前用户更新对应公司流水号
        if(IdDesignEnableEnum.TRUE.getValue().equals(idDesign.getEnableCompany())) {
            // 获取当前用户后，更新当前流水号+1
            designIdCompanyService.updateNumberPlusOne(idDesign.getId(), currentCompanyId);
        } else {
            LambdaUpdateWrapper<IdDesign> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(IdDesign::getId, idDesign.getId())
                    .set(IdDesign::getSerialNumberCurrent, idDesign.getSerialNumberCurrent() + 1);
            this.update(wrapper);
        }
    }

    // 根据当前用户获取公司隔离信息
    private DesignIdCompany getCompanyIsolationByCurrentUser(IdDesign idDesign, Long currentCompanyId) {
        // 获取当前用户后，判断当前流水号下是否存在对应公司值
        DesignIdCompany designIdCompany;
        if(!designIdCompanyService.isExisted(idDesign.getId(), currentCompanyId)) {
            // 不存在则创建
            designIdCompany = designIdCompanyService.create(idDesign.getId(), currentCompanyId, idDesign.getSerialNumberStart());
        } else {
            // 存在则获取
            designIdCompany = designIdCompanyService.selectByDesignAndCompanyId(idDesign.getId(), currentCompanyId);
        }
        return designIdCompany;
    }
}