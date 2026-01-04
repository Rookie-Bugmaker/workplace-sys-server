package com.crrc.sys.cache;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.crrc.sys.config.redisson.RedissonManager;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.entity.dict.Dict;
import com.crrc.sys.entity.dict.DictItem;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.entity.user.SysUserFindByPageParam;
import com.crrc.sys.service.DictCacheService;
import com.crrc.sys.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DictCachePreheat implements ApplicationRunner {

    private static final int BATCH_SIZE = 1000; // 每批处理1000条记录

    @Autowired
    private DictCacheService dictCacheService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SysUserService userService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("开始预热系统缓存...");
        long startTime = System.currentTimeMillis();

        // 1. 并行加载字典并缓存
        Map<Long, Dict> dictMap = loadAndCacheDicts();

        // 2. 分批加载并缓存字典项
        AtomicInteger cachedItemCount = new AtomicInteger(0);
        batchProcessDictItems(dictMap, cachedItemCount);

        // 3. 预热用户缓存
//        AtomicInteger cachedUserCount = new AtomicInteger(0);
//        batchProcessUsers(cachedUserCount);

        // 4. 预热部门缓存
//        AtomicInteger cachedDeptCount = new AtomicInteger(0);
//        batchProcessDepts(cachedDeptCount);

        log.info("系统缓存预热完成，共处理字典{}个，字典项{}个，总耗时{}ms",
                dictMap.size(),
                cachedItemCount.get(),
                System.currentTimeMillis() - startTime);
    }

    /**
     * 并行加载字典并缓存（跳过未变化的字典）
     */
    private Map<Long, Dict> loadAndCacheDicts() {
        return Db.lambdaQuery(Dict.class)
                .list()
                .parallelStream()
                .filter(dict -> {
                    // 检查字典是否需要更新
                    boolean needUpdate = dictCacheService.needUpdateDict(dict);
                    if (needUpdate) {
                        dictCacheService.setDictCache(dict);
                    }
                    return true; // 保留所有字典用于后续处理
                })
                .collect(Collectors.toConcurrentMap(Dict::getId, Function.identity()));
    }

    /**
     * 分批处理字典项（提高性能并减少内存消耗）
     */
    private void batchProcessDictItems(Map<Long, Dict> dictMap, AtomicInteger cachedItemCount) {
        int currentPage = 1;
        boolean hasMore = true;

        while (hasMore) {
            Page<DictItem> page = new Page<>(currentPage, BATCH_SIZE);
            List<DictItem> items = Db.lambdaQuery(DictItem.class)
                    .orderByAsc(DictItem::getId)
                    .page(page)
                    .getRecords();

            if (items.isEmpty()) {
                hasMore = false;
            } else {
                // 并行处理当前批次的字典项
                items.parallelStream().forEach(item -> {
                    Dict dict = dictMap.get(item.getDictId());
                    if (dict != null && dictCacheService.needUpdateItem(item, dict)) {
                        dictCacheService.setDictItemCache(item, dict);
                        cachedItemCount.incrementAndGet();
                    }
                });
                currentPage++;
            }
        }
    }

    /**
     * 分批处理用户数据并缓存
     */
//    private void batchProcessUsers(AtomicInteger cachedUserCount) {
//
//        SysUserFindByPageParam sysUserFindByPageParam = new SysUserFindByPageParam();
//        sysUserFindByPageParam.setPage(1);
//        sysUserFindByPageParam.setPageSize(0);
//        List<SysUser> users = userService.findByPage(sysUserFindByPageParam).getRows();
//        // 并行处理当前批次的用户
//        users.parallelStream().forEach(user -> {
//            String userKey = CacheKeyGenerator.getUserExtendKey(user.getUserId().toString());
//            String lockKey = CacheKeyGenerator.getLockKey(userKey);
//
//            LockUtil.executeWithLock(RedissonManager.getRedissonClient(), lockKey, () -> {
//                Map<String, String> userMap = new HashMap<>();
//                userMap.put("userId", String.valueOf(user.getUserId()));
//                userMap.put("userName", StringUtils.defaultIfBlank(user.getUserName(), ""));
//                userMap.put("nickName", StringUtils.defaultIfBlank(user.getUserDescription(), ""));
//                redisCache.setCacheMap(userKey, userMap);
//                cachedUserCount.incrementAndGet();
//            });
//        });
//    }

    /**
     * 分批处理部门数据并缓存
     */
//    private void batchProcessDepts(AtomicInteger cachedDeptCount) {
//        int currentPage = 1;
//        boolean hasMore = true;
//
//        while (hasMore) {
//            Page<Dept> page = new Page<>(currentPage, BATCH_SIZE);
//            List<Dept> depts = Db.lambdaQuery(Dept.class)
//                    .orderByAsc(Dept::getId)
//                    .page(page)
//                    .getRecords();
//
//            if (depts.isEmpty()) {
//                hasMore = false;
//            } else {
//                // 并行处理当前批次的部门
//                depts.parallelStream().forEach(dept -> {
//                    String deptKey = CacheKeyGenerator.getDeptExtendKey(dept.getId().toString());
//                    String lockKey = CacheKeyGenerator.getLockKey(deptKey);
//
//                    LockUtil.executeWithLock(RedissonManager.getRedissonClient(), lockKey, () -> {
//                        Map<String, String> deptMap = new HashMap<>();
//                        deptMap.put("deptId", String.valueOf(dept.getId()));
//                        deptMap.put("orgCode", StringUtils.defaultIfBlank(dept.getOrgCode(), ""));
//                        deptMap.put("departmentName", StringUtils.defaultIfBlank(dept.getDepartmentName(), ""));
//                        redisCache.setCacheMap(deptKey, deptMap);
//                        cachedDeptCount.incrementAndGet();
//                    });
//                });
//                currentPage++;
//            }
//        }
//    }
}