package com.crrc.sys.cache;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

public class LockUtil {
    private static final long WAIT_TIME = 5L;
    private static final long LEASE_TIME = 30L;

    public static void executeWithLock(RedissonClient redissonClient,
                                       String lockKey,
                                       Runnable action) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS);
            if (locked) {
                action.run();
            } else {
                throw new RuntimeException("获取锁超时，lockKey: " + lockKey);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("锁获取被中断", e);
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}