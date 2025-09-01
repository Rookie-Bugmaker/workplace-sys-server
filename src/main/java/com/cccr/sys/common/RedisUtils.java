package com.cccr.sys.common;

import com.cccr.sys.config.exception.ParamValidateException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author 谭永强
 * @date 2023-08-15
 */
@Component
public class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 写入缓存
     *
     * @param key   建
     * @param value 值
     * @return 成功/失败
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 写入缓存设置时效时间(s)
     *
     * @param key        键
     * @param value      值
     * @param expireTime 超时时间
     * @return 成功/失败
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 判断缓存中是否有对应的value
     *
     * @param key 键
     * @return 成功/失败
     */
    public boolean exists(final String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 读取缓存
     *
     * @param key 键
     * @return 成功/失败
     */
    public Object get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除对应的value
     *
     * @param key 键
     * @return 成功/失败
     */
    public boolean remove(final String key) {
        if (exists(key)) {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        }
        return false;
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return 结果
     */
    public Long incr(String key, long delta) {
        if (ObjectUtils.isEmpty(key)) {
            throw new ParamValidateException("key值不能为空");
        }
        if (delta < 0) {
            throw new ParamValidateException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return 结果
     */
    public Long decr(String key, long delta) {
        if (ObjectUtils.isEmpty(key)) {
            throw new ParamValidateException("key值不能为空");
        }
        if (delta < 0) {
            throw new ParamValidateException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * 设置键值(原子性)
     *
     * @param key   键
     * @param value 值
     * @param time  缓存时间，单位为s
     * @return true：上锁成功，false：上失败，该锁正在被使用
     */
    public boolean setNx(String key, Object value, long time) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, time, TimeUnit.SECONDS));
    }

    /**
     * 设置键值(原子性)
     *
     * @param key   键
     * @param value 值
     * @return true：上锁成功，false：上失败，该锁正在被使用
     */
    public boolean setNx(String key, Object value) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value));
    }
}
