package com.crrc.sys.service.impl;

import com.alibaba.nacos.shaded.com.google.common.collect.Sets;
import com.crrc.sys.cache.CacheKeyGenerator;
import com.crrc.sys.cache.LockUtil;
import com.crrc.sys.cache.RedisCache;
import com.crrc.sys.config.redisson.RedissonManager;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.entity.dict.Dict;
import com.crrc.sys.entity.dict.DictItem;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.model.dict.DictItemExcelVO;
import com.crrc.sys.model.dict.DictItemVO;
import com.crrc.sys.model.dict.DictVO;
import com.crrc.sys.service.DictCacheService;
import com.crrc.sys.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DictCacheServiceImpl implements DictCacheService {
    @Autowired
    private RedisCache redisCache;

    @Override
    public boolean needUpdateDict(Dict dict) {
        if (dict == null || StringUtils.isBlank(dict.getDictCode())) {
            return false;
        }

        String dictKey = CacheKeyGenerator.getDictKey(dict.getDictCode());
        Map<String, Object> cachedDict = redisCache.getCacheMap(dictKey);

        // 如果缓存不存在，则需要更新
        if (cachedDict == null || cachedDict.isEmpty()) {
            return true;
        }

        // 比较字典各字段是否变化
        return !StringUtils.equals(String.valueOf(cachedDict.get("dictCode")), dict.getDictCode()) ||
                !StringUtils.equals(String.valueOf(cachedDict.get("dictName")), dict.getDictName()) ||
                !StringUtils.equals(String.valueOf(cachedDict.get("dictType")), dict.getDictType()) ||
                !StringUtils.equals(String.valueOf(cachedDict.get("remark")), dict.getRemark());
    }

    @Override
    public boolean needUpdateItem(DictItem dictItem, Dict dict) {
        if (dictItem == null || dict == null ||
                StringUtils.isBlank(dict.getDictCode()) ||
                StringUtils.isBlank(dictItem.getItemCode())) {
            return false;
        }

        String itemKey = CacheKeyGenerator.getDictItemKey(dict.getDictCode(), dictItem.getItemCode());
        Map<String, String> cachedItem = redisCache.getCacheMap(itemKey);

        // 如果缓存不存在，则需要更新
        if (cachedItem == null || cachedItem.isEmpty()) {
            return true;
        }

        // 比较字典项各字段是否变化
        return !StringUtils.equals(cachedItem.get("itemCode"), dictItem.getItemCode()) ||
                !StringUtils.equals(cachedItem.get("itemName"), dictItem.getItemName()) ||
                !StringUtils.equals(cachedItem.get("sorted"), dictItem.getSorted());
    }

    @Override
    public void setDictCache(Dict dict) {
        if (dict == null || StringUtils.isBlank(dict.getDictCode())) {
            throw new IllegalArgumentException("字典编码不能为空");
        }

        // 只有需要更新时才执行
        if (!needUpdateDict(dict)) {
            return;
        }

        String dictKey = CacheKeyGenerator.getDictKey(dict.getDictCode());
        String lockKey = CacheKeyGenerator.getLockKey(dictKey);

        LockUtil.executeWithLock(RedissonManager.getRedissonClient(), lockKey, () -> {
            // 更新字典索引
            redisCache.setCacheMapValue(CacheKeyGenerator.getDictIndexKey(),
                    dict.getDictCode(), dict.getDictName());
            // 更新字典数据
            redisCache.setCacheMap(dictKey, convertDictToMap(dict));
        });
    }
    
    @Override
    public void deleteDictCache(Dict dict) {
        if (dict == null || StringUtils.isBlank(dict.getDictCode())) {
            return;
        }
        
        String dictKey = CacheKeyGenerator.getDictKey(dict.getDictCode());
        String lockKey = CacheKeyGenerator.getLockKey(dictKey);
        
        LockUtil.executeWithLock(RedissonManager.getRedissonClient(), lockKey, () -> {
            // 删除字典索引
            redisCache.delCacheMapValue(CacheKeyGenerator.getDictIndexKey(), dict.getDictCode());
            // 删除字典数据
            redisCache.deleteObject(dictKey);
            // 删除关联的所有字典项
            clearDictItemsCache(dict.getDictCode());
            log.info("字典缓存删除成功，dictCode: {}", dict.getDictCode());
        });
    }

    @Override
    public void setDictItemCache(DictItem dictItem, Dict dict) {
        if (dictItem == null || dict == null ||
                StringUtils.isBlank(dict.getDictCode()) ||
                StringUtils.isBlank(dictItem.getItemCode())) {
            throw new IllegalArgumentException("字典项参数不完整");
        }

        // 只有需要更新时才执行
        if (!needUpdateItem(dictItem, dict)) {
            return;
        }

        String itemKey = CacheKeyGenerator.getDictItemKey(dict.getDictCode(), dictItem.getItemCode());
        String lockKey = CacheKeyGenerator.getLockKey(itemKey);

        LockUtil.executeWithLock(RedissonManager.getRedissonClient(), lockKey, () -> {
            redisCache.setCacheMap(itemKey, convertDictItemToMap(dictItem));
        });
    }
    
    @Override
    public void deleteDictItemCache(DictItem dictItem, Dict dict) {
        if (dictItem == null || dict == null || 
            StringUtils.isBlank(dict.getDictCode()) || 
            StringUtils.isBlank(dictItem.getItemCode())) {
            return;
        }
        
        String itemKey = CacheKeyGenerator.getDictItemKey(dict.getDictCode(), dictItem.getItemCode());
        String lockKey = CacheKeyGenerator.getLockKey(itemKey);
        
        LockUtil.executeWithLock(RedissonManager.getRedissonClient(), lockKey, () -> {
            redisCache.deleteObject(itemKey);
            log.info("字典项缓存删除成功，dictCode: {}, itemCode: {}", 
                    dict.getDictCode(), dictItem.getItemCode());
        });
    }

    @Override
    public void loadDictItemCache(Dict dict, List<DictItem> items) {
        if (!CollectionUtils.isEmpty(items)) {
            items.forEach(item -> {
                setDictItemCache(item, dict);
            });
        }
    }

    @Override
    public void batchSetDictItemCache(List<DictItemExcelVO> items) {
        if (CollectionUtils.isEmpty(items)) {
            return;
        }
        
        // 按字典分组处理
        Map<String, List<DictItemExcelVO>> groupedItems = items.stream()
            .collect(Collectors.groupingBy(DictItemExcelVO::getDictCode));
        
        groupedItems.forEach((dictCode, itemList) -> {
            String dictKey = CacheKeyGenerator.getDictKey(dictCode);
            String lockKey = CacheKeyGenerator.getLockKey(dictKey);
            
            LockUtil.executeWithLock(RedissonManager.getRedissonClient(), lockKey, () -> {
                itemList.forEach(item -> {
                    String itemKey = CacheKeyGenerator.getDictItemKey(dictCode, item.getItemCode());
                    redisCache.setCacheMap(itemKey, convertDictItemToMap(item));
                });
                log.info("批量导入字典项缓存成功，dictCode: {}, count: {}", 
                        dictCode, itemList.size());
            });
        });
    }
    
    @Override
    public void clearDictCache(String dictCode) {
        if (StringUtils.isBlank(dictCode)) {
            return;
        }
        
        String dictKey = CacheKeyGenerator.getDictKey(dictCode);
        String lockKey = CacheKeyGenerator.getLockKey(dictKey);
        
        LockUtil.executeWithLock(RedissonManager.getRedissonClient(), lockKey, () -> {
            // 删除字典数据
            redisCache.deleteObject(dictKey);
            // 删除关联的所有字典项
            clearDictItemsCache(dictCode);
            log.info("字典缓存清空成功，dictCode: {}", dictCode);
        });
    }


    private void clearDictItemsCache(String dictCode) {
        String pattern = CacheKeyGenerator.getDictKey(dictCode) + "*";
        Iterable<String> keys = redisCache.keys(pattern);
        keys.forEach(key -> {
            redisCache.deleteObject(key);
        });
    }
    
    // 转换方法
    private Map<String, Object> convertDictToMap(Dict dict) {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        resultMap.put("dictCode", StringUtils.defaultIfBlank(dict.getDictCode(), ""));
        resultMap.put("dictName", StringUtils.defaultIfBlank(dict.getDictName(), ""));
        resultMap.put("dictType", StringUtils.defaultIfBlank(dict.getDictType(), ""));
        resultMap.put("remark", StringUtils.defaultIfBlank(dict.getRemark(), ""));
        return resultMap;
    }
    
    private Map<String, String> convertDictItemToMap(DictItem dictItem) {
        Map<String, String> itemMap = new LinkedHashMap<>();
        itemMap.put("itemCode", StringUtils.defaultIfBlank(dictItem.getItemCode(), ""));
        itemMap.put("itemName", StringUtils.defaultIfBlank(dictItem.getItemName(), ""));
        itemMap.put("sorted", StringUtils.defaultIfBlank(dictItem.getSorted(), "0"));
        return itemMap;
    }
    
    private Map<String, String> convertDictItemToMap(DictItemExcelVO dictItem) {
        Map<String, String> itemMap = new LinkedHashMap<>();
        itemMap.put("itemCode", StringUtils.defaultIfBlank(dictItem.getItemCode(), ""));
        itemMap.put("itemName", StringUtils.defaultIfBlank(dictItem.getItemName(), ""));
        itemMap.put("sorted", StringUtils.defaultIfBlank(dictItem.getSorted(), "0"));
        return itemMap;
    }

    @Override
    public DictVO getDictFromCache(String dictCode) {
        String dictKey = CacheKeyGenerator.getDictKey(dictCode);
        String itemPrefix = CacheKeyGenerator.getDictItemKey(dictCode, "");

        // 获取字典基本信息
        Map<String, Object> dictMap = redisCache.getCacheMap(dictKey);
        if (dictMap == null || dictMap.isEmpty()) {
            return null;
        }

        DictVO dictVO = new DictVO();
        dictVO.setDictCode(String.valueOf(dictMap.get("dictCode")));
        dictVO.setDictName(String.valueOf(dictMap.get("dictName")));
        dictVO.setDictType(String.valueOf(dictMap.get("dictType")));
        dictVO.setRemark(String.valueOf(dictMap.get("remark")));
        dictVO.setItems(new ArrayList<>());

        // 获取字典项
        Set<String> itemKeys = Sets.newHashSet(redisCache.keys(itemPrefix + "*"));
        for (String key : itemKeys) {
            Map<String, String> itemMap = redisCache.getCacheMap(key);
            if (itemMap != null && !itemMap.isEmpty()) {
                DictItemVO itemVO = new DictItemVO();
                itemVO.setItemCode(itemMap.get("itemCode"));
                itemVO.setItemName(itemMap.get("itemName"));
                itemVO.setSorted(itemMap.get("sorted"));
                dictVO.getItems().add(itemVO);
            }
        }

        return dictVO;
    }

    @Override
    public String getDictNameFromCache(String dictCode) {
        return redisCache.getCacheMapValue(CacheKeyGenerator.getDictIndexKey(), dictCode);
    }

    @Override
    public List<String> getDictCodesFromCache(String dictName) {
        Map<String, String> indexMap = redisCache.getCacheMap(CacheKeyGenerator.getDictIndexKey());
        if (indexMap == null || indexMap.isEmpty()) {
            return Collections.emptyList();
        }

        return indexMap.entrySet().stream()
                .filter(entry -> dictName.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<DictVO> getAllDictsFromCache() {
        Map<String, String> indexMap = redisCache.getCacheMap(CacheKeyGenerator.getDictIndexKey());
        if (indexMap == null || indexMap.isEmpty()) {
            return Collections.emptyList();
        }

        return indexMap.keySet().stream()
                .map(this::getDictFromCache)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public DictItemVO getDictItemFromCache(String dictCode, String itemCode) {
        String itemKey = CacheKeyGenerator.getDictItemKey(dictCode, itemCode);
        Map<String, String> itemMap = redisCache.getCacheMap(itemKey);
        if (itemMap == null || itemMap.isEmpty()) {
            return null;
        }

        DictItemVO itemVO = new DictItemVO();
        itemVO.setItemCode(itemMap.get("itemCode"));
        itemVO.setItemName(itemMap.get("itemName"));
        itemVO.setSorted(itemMap.get("sorted"));
        return itemVO;
    }

    @Override
    public String getDictItemNameFromCache(String dictCode, String itemCode) {
        DictItemVO itemVO = getDictItemFromCache(dictCode, itemCode);
        return itemVO != null ? itemVO.getItemName() : null;
    }

    @Override
    public String getDictItemCodeFromCache(String dictCode, String itemName) {
        String itemPrefix = CacheKeyGenerator.getDictItemKey(dictCode, "");
        Set<String> itemKeys = Sets.newHashSet(redisCache.keys(itemPrefix + "*"));

        for (String key : itemKeys) {
            Map<String, String> itemMap = redisCache.getCacheMap(key);
            if (itemMap != null && itemName.equals(itemMap.get("itemName"))) {
                return itemMap.get("itemCode");
            }
        }
        return null;
    }

    @Override
    public void cacheDict(DictVO dictVO) {
        if (dictVO == null || StringUtils.isBlank(dictVO.getDictCode())) {
            return;
        }

        String dictKey = CacheKeyGenerator.getDictKey(dictVO.getDictCode());
        String lockKey = CacheKeyGenerator.getLockKey(dictKey);

        LockUtil.executeWithLock(RedissonManager.getRedissonClient(), lockKey, () -> {
            // 更新字典索引
            redisCache.setCacheMapValue(CacheKeyGenerator.getDictIndexKey(),
                    dictVO.getDictCode(), dictVO.getDictName());
            // 更新字典数据 - 确保不放入null值
            Map<String, Object> dictMap = new HashMap<>();
            dictMap.put("dictCode", StringUtils.defaultIfBlank(dictVO.getDictCode(), ""));
            dictMap.put("dictName", StringUtils.defaultIfBlank(dictVO.getDictName(), ""));
            dictMap.put("dictType", StringUtils.defaultIfBlank(dictVO.getDictType(),""));
            dictMap.put("remark", StringUtils.defaultIfBlank(dictVO.getRemark(),""));
            redisCache.setCacheMap(dictKey, dictMap);

            // 更新字典项
            if (!CollectionUtils.isEmpty(dictVO.getItems())) {
                dictVO.getItems().forEach(item -> cacheDictItem(dictVO.getDictCode(), item));
            }
        });
    }

    @Override
    public void cacheDictItem(String dictCode, DictItemVO itemVO) {
        if (StringUtils.isBlank(dictCode) || itemVO == null || StringUtils.isBlank(itemVO.getItemCode())) {
            return;
        }

        String itemKey = CacheKeyGenerator.getDictItemKey(dictCode, itemVO.getItemCode());
        String lockKey = CacheKeyGenerator.getLockKey(itemKey);

        LockUtil.executeWithLock(RedissonManager.getRedissonClient(), lockKey, () -> {
            Map<String, String> itemMap = new HashMap<>();
            itemMap.put("itemCode", StringUtils.defaultIfBlank(itemVO.getItemCode(),""));
            itemMap.put("itemName", StringUtils.defaultIfBlank(itemVO.getItemName(),""));
            itemMap.put("sorted", StringUtils.defaultIfBlank(itemVO.getSorted(),""));
            redisCache.setCacheMap(itemKey, itemMap);
        });
    }

//    @Override
//    public String getUserRealNameInDictExtendsFromCache(Long userId, String userName) {
//        if (userId != null) {
//            // 按userId查询
//            String userKey = CacheKeyGenerator.getUserExtendKey(userId.toString());
//            Map<String, String> userMap = redisCache.getCacheMap(userKey);
//            if (userMap != null && !userMap.isEmpty()) {
//                return StringUtils.defaultIfBlank(userMap.get("nickName"), "");
//            }
//        }
//
//        if (StringUtils.isNotBlank(userName)) {
//            // 按userName查询（需要遍历所有用户缓存）
//            String userPattern = CacheKeyGenerator.getUserExtendKey("*");
//            Set<String> userKeys = Sets.newHashSet(redisCache.keys(userPattern));
//
//            for (String key : userKeys) {
//                Map<String, String> userMap = redisCache.getCacheMap(key);
//                if (userMap != null && userName.equals(userMap.get("userName"))) {
//                    return StringUtils.defaultIfBlank(userMap.get("nickName"), "");
//                }
//            }
//        }
//
//        return "";
//    }

//    @Override
//    public String getDeptNameNameInDictExtendsFromCache(Long deptId, String orgCode) {
//        if (deptId != null) {
//            // 按deptId查询
//            String deptKey = CacheKeyGenerator.getDeptExtendKey(deptId.toString());
//            Map<String, String> deptMap = redisCache.getCacheMap(deptKey);
//            if (deptMap != null && !deptMap.isEmpty()) {
//                return StringUtils.defaultIfBlank(deptMap.get("departmentName"), "");
//            }
//        }
//
//        if (StringUtils.isNotBlank(orgCode)) {
//            // 按orgCode查询（需要遍历所有部门缓存）
//            String deptPattern = CacheKeyGenerator.getDeptExtendKey("*");
//            Set<String> deptKeys = Sets.newHashSet(redisCache.keys(deptPattern));
//
//            for (String key : deptKeys) {
//                Map<String, String> deptMap = redisCache.getCacheMap(key);
//                if (deptMap != null && orgCode.equals(deptMap.get("orgCode"))) {
//                    return StringUtils.defaultIfBlank(deptMap.get("departmentName"), "");
//                }
//            }
//        }
//
//        return "";
//    }

//    @Override
//    public void cacheUserRealInDictExtends(SysUser user) {
//        if (user == null || user.getUserId() == null) {
//            return;
//        }
//
//        String userKey = CacheKeyGenerator.getUserExtendKey(user.getUserId().toString());
//        String lockKey = CacheKeyGenerator.getLockKey(userKey);
//
//        LockUtil.executeWithLock(RedissonManager.getRedissonClient(), lockKey, () -> {
//            Map<String, String> userMap = new HashMap<>();
//            userMap.put("userId", String.valueOf(user.getUserId()));
//            userMap.put("userName", StringUtils.defaultIfBlank(user.getUserName(), ""));
//            userMap.put("nickName", StringUtils.defaultIfBlank(user.getUserDescription(), ""));
//            redisCache.setCacheMap(userKey, userMap);
//        });
//    }

//    @Override
//    public void cacheDeptNameInDictExtends(Dept dept) {
//        if (dept == null || dept.getId() == null) {
//            return;
//        }
//
//        String deptKey = CacheKeyGenerator.getDeptExtendKey(dept.getId().toString());
//        String lockKey = CacheKeyGenerator.getLockKey(deptKey);
//
//        LockUtil.executeWithLock(RedissonManager.getRedissonClient(), lockKey, () -> {
//            Map<String, String> deptMap = new HashMap<>();
//            deptMap.put("deptId", String.valueOf(dept.getId()));
//            deptMap.put("orgCode", StringUtils.defaultIfBlank(dept.getOrgCode(), ""));
//            deptMap.put("departmentName", StringUtils.defaultIfBlank(dept.getDepartmentName(), ""));
//            redisCache.setCacheMap(deptKey, deptMap);
//        });
//    }
}