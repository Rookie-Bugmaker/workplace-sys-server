package com.crrc.sys.cache;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.crrc.sys.config.redisson.RedissonManager;
import com.crrc.sys.entity.api.SysApi;
import com.crrc.sys.entity.api.SysApiDBEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 系统API缓存预热
 * 系统启动时将SysApi加载到Redis中
 *
 * @author shenchuanliang
 * @date 2026-06-25
 */
@Component
@Slf4j
public class SysApiCachePreheat implements ApplicationRunner {

    private static final int BATCH_SIZE = 1000; // 每批处理1000条记录

    @Autowired
    private RedisCache redisCache;

    @Override
    public void run(ApplicationArguments args) {
        log.info("开始预热API缓存...");
        long startTime = System.currentTimeMillis();

        // 分批加载API并缓存
        AtomicInteger cachedApiCount = batchProcessApis();

        log.info("API缓存预热完成，共处理API{}个，总耗时{}ms",
                cachedApiCount.get(),
                System.currentTimeMillis() - startTime);
    }

    /**
     * 分批处理API数据并缓存
     */
    private AtomicInteger batchProcessApis() {
        AtomicInteger cachedApiCount = new AtomicInteger(0);
        int currentPage = 1;
        boolean hasMore = true;

        while (hasMore) {
            Page<SysApiDBEntity> page = new Page<>(currentPage, BATCH_SIZE);
            List<SysApiDBEntity> apis = Db.lambdaQuery(SysApiDBEntity.class)
                    .orderByAsc(SysApiDBEntity::getApiId)
                    .page(page)
                    .getRecords();

            if (apis.isEmpty()) {
                hasMore = false;
            } else {
                // 并行处理当前批次的API
                List<SysApi> apiList = apis.parallelStream()
                        .map(entity -> {
                            SysApi api = new SysApi();
                            api.setId(entity.getApiId());
                            api.setApiName(entity.getApiName());
                            api.setApiUri(entity.getApiUri());
                            api.setApiMethod(entity.getApiMethod());
                            api.setApiDesc(entity.getApiDesc());
                            return api;
                        })
                        .collect(Collectors.toList());

                // 缓存API列表
                cachedApiCount.addAndGet(apiList.size());

                // 缓存每个API的URI索引
                apiList.forEach(api -> {
                    String uriKey = CacheKeyGenerator.getSysApiByUriKey(api.getApiUri());
                    String lockKey = CacheKeyGenerator.getLockKey(uriKey);
                    LockUtil.executeWithLock(RedissonManager.getRedissonClient(), lockKey, () -> {
                        redisCache.setCacheObject(uriKey, api.getApiUri());
                    });
                });

                currentPage++;
            }
        }

        return cachedApiCount;
    }
}
