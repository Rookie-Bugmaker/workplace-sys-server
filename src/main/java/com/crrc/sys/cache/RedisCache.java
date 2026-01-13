package com.crrc.sys.cache;

import com.crrc.sys.config.redisson.RedissonManager;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings(value = {"unchecked", "rawtypes"})
@Component
public class RedisCache {

    private RedissonClient getRedissonClient(){
        return RedissonManager.getRedissonClient();
    }

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        getRedissonClient().getBucket(key).set(value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        getRedissonClient().getBucket(key).set(value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return getRedissonClient().getBucket(key).expire(timeout, unit);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key) {

        return (T) getRedissonClient().getBucket(key).get();
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key) {
        return getRedissonClient().getBucket(key).delete();
    }


    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> boolean setCacheList(final String key, final List<T> dataList) {
        return getRedissonClient().getList(key).addAll(dataList);
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key) {
        return (List<T>) getRedissonClient().getList(key).readAll();
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> boolean setCacheSet(final String key, final Set<T> dataSet) {
        return getRedissonClient().getSet(key).addAll(dataSet);
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(final String key) {
        return (Set<T>) getRedissonClient().getSet(key).readAll();
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMapSync(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            RLock lock = getRedissonClient().getLock(key + "_lock");
            if (lock.tryLock()) {
                getRedissonClient().getMapCache(key).putAll(dataMap);
            }
        }
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            getRedissonClient().getMapCache(key).putAll(dataMap);
        }
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap, final long ttl, TimeUnit timeUnit) {
        if (dataMap != null) {
            getRedissonClient().getMapCache(key).putAll(dataMap, ttl, timeUnit);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(final String key) {
        return getRedissonClient().getMapCache(key);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        getRedissonClient().getMapCache(key).put(hKey, value);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value, final long ttl, TimeUnit timeUnit) {
        getRedissonClient().getMapCache(key).put(hKey, value, ttl, timeUnit);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey) {

        return (T) getRedissonClient().getMapCache(key).get(hKey);
    }

    /**
     * 删除Hash中的数据
     *
     * @param key
     * @param hkey
     */
    public void delCacheMapValue(final String key, final String hkey) {
        getRedissonClient().getMapCache(key).remove(hkey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> Map<Object, Object> getMultiCacheMapValue(final String key, final Set<Object> hKeys) {
        return getRedissonClient().getMapCache(key).getAll(hKeys);
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Iterable<String> keys(final String pattern) {
        // return getRedissonClient().getKeys().getKeysByPattern(pattern);
        // 使用SCAN命令替代KEYS
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(100).build();
        Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(options);

        return () -> new Iterator<String>() {
            @Override
            public boolean hasNext() {
                boolean hasNext = cursor.hasNext();
                if (!hasNext) {
                    cursor.close();
                }
                return hasNext;
            }

            @Override
            public String next() {
                return new String(cursor.next());
            }
        };
    }
}