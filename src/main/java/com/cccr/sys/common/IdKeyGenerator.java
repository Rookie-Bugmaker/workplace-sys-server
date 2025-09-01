package com.cccr.sys.common;

import com.cccr.sys.config.exception.BizException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 全局ID生成(此类优化了雪花ID在容器环境下ID生成重复的问题)
 *
 * @author 谭永强
 * @date 2022-08-31
 */
@Component
public class IdKeyGenerator {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 雪花算法ID重复优化
     * 1.定义一个 redis 作为缓存 key，然后服务每次初始化的时候都 incr 这个 key。
     * 2.上面得到的 incr 的结果然后与 1024 取模。取模可以优化为：result & 0x000003FF
     */
    @PostConstruct
    public void init() {
        String cacheKey = "order_snowflake_workerId_incr";
        //在服务每次上线的时候就会把之前的 incr 值加 1。然后与 1024 取模，最后 workerId 就会一直在 [0 ~ 1023] 范围内进行动态取值。
        Long increment = stringRedisTemplate.opsForValue().increment(cacheKey);
        if (increment == null) {
            throw new BizException("雪花算法-workerId初始化失败");
        }
        long workerId = increment & 0x000003FF;
        snowflakeIdWorker = new SnowflakeIdWorker(workerId);
    }

    /**
     * 获取雪花ID
     *
     * @return 雪花ID
     */
    public long getId() {
        if (ObjectUtils.isEmpty(snowflakeIdWorker)) {
            throw new BizException("雪花算法初始化失败");
        }
        return snowflakeIdWorker.nextId();
    }
}
