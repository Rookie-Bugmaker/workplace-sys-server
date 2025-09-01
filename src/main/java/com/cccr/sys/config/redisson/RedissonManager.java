package com.cccr.sys.config.redisson;

import com.cccr.sys.common.SpringUtil;
import com.cccr.sys.config.redis.RedisConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import java.util.stream.Collectors;

/**
 * Redisson管理器
 *
 * @author 谭永强
 * @date 2023-01-11
 */
public class RedissonManager {

    private RedissonManager() {
    }

    private static class RedissonManagerHolder {
        private static final RedissonClient redissonClient;
        private static final Config CONFIG = new Config();
        static {
            RedisConfig redisConfig = SpringUtil.getBean(RedisConfig.class);
            RedisProperties redisProperties = redisConfig.getRedisProperties();
            String type = redisConfig.getType();
            switch (type) {
                case "cluster":
                    //集群模式，待添加....
                    break;
                case "sentinel":
                    //哨兵模式
                    CONFIG.useSentinelServers()
                            .setConnectTimeout(30000)
                            .setPassword(redisProperties.getPassword())
                            .setMasterName(redisProperties.getSentinel().getMaster())
                            .setSentinelPassword(redisProperties.getSentinel().getPassword())
                            .setSentinelAddresses(redisProperties.getSentinel().getNodes().stream().map(o -> "redis://" + o).collect(Collectors.toList()));
                    break;
                default:
                    //单机模式
                    CONFIG.useSingleServer()
                            .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort())
                            .setDatabase(redisProperties.getDatabase())
                            .setPassword(redisProperties.getPassword());
                    break;
            }
            //得到redisson对象
            redissonClient = Redisson.create(CONFIG);
        }
    }

    /**
     * 获取redisson对象的方法
     *
     * @return Redisson
     */
    public static RedissonClient getRedissonClient() {
        return RedissonManagerHolder.redissonClient;
    }
}
