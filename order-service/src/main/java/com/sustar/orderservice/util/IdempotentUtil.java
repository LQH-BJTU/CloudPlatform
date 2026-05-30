package com.sustar.orderservice.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 幂等处理工具类
 * 用于防止重复请求、重复回调等场景
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotentUtil {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 幂等键前缀
     */
    private static final String IDEMPOTENT_PREFIX = "order:idempotent:";

    /**
     * 默认过期时间（分钟）
     */
    private static final int DEFAULT_EXPIRE_MINUTES = 30;

    /**
     * 尝试获取幂等锁
     *
     * @param key      幂等键
     * @param expireMinutes 过期时间（分钟）
     * @return 是否获取成功
     */
    public boolean tryAcquire(String key, int expireMinutes) {
        String redisKey = IDEMPOTENT_PREFIX + key;
        Boolean success = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", expireMinutes, TimeUnit.MINUTES);
        boolean result = Boolean.TRUE.equals(success);
        if (result) {
            log.debug("获取幂等锁成功，key={}", key);
        } else {
            log.debug("获取幂等锁失败，key={}", key);
        }
        return result;
    }

    /**
     * 尝试获取幂等锁（使用默认过期时间）
     *
     * @param key 幂等键
     * @return 是否获取成功
     */
    public boolean tryAcquire(String key) {
        return tryAcquire(key, DEFAULT_EXPIRE_MINUTES);
    }

    /**
     * 释放幂等锁
     *
     * @param key 幂等键
     */
    public void release(String key) {
        String redisKey = IDEMPOTENT_PREFIX + key;
        redisTemplate.delete(redisKey);
        log.debug("释放幂等锁，key={}", key);
    }

    /**
     * 检查是否存在幂等键
     *
     * @param key 幂等键
     * @return 是否存在
     */
    public boolean exists(String key) {
        String redisKey = IDEMPOTENT_PREFIX + key;
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }

    /**
     * 设置幂等键（带值）
     *
     * @param key   幂等键
     * @param value 值
     * @param expireMinutes 过期时间（分钟）
     * @return 是否设置成功
     */
    public boolean setIfAbsent(String key, String value, int expireMinutes) {
        String redisKey = IDEMPOTENT_PREFIX + key;
        Boolean success = redisTemplate.opsForValue().setIfAbsent(redisKey, value, expireMinutes, TimeUnit.MINUTES);
        return Boolean.TRUE.equals(success);
    }

    /**
     * 获取幂等键的值
     *
     * @param key 幂等键
     * @return 值
     */
    public String get(String key) {
        String redisKey = IDEMPOTENT_PREFIX + key;
        return redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 设置幂等键的值（覆盖）
     *
     * @param key   幂等键
     * @param value 值
     * @param expireMinutes 过期时间（分钟）
     */
    public void set(String key, String value, int expireMinutes) {
        String redisKey = IDEMPOTENT_PREFIX + key;
        redisTemplate.opsForValue().set(redisKey, value, expireMinutes, TimeUnit.MINUTES);
    }

    /**
     * 生成支付回调幂等键
     *
     * @param orderNo 订单编号
     * @param payNo   支付流水号
     * @return 幂等键
     */
    public String generatePayCallbackKey(String orderNo, String payNo) {
        return "pay:callback:" + orderNo + ":" + payNo;
    }

    /**
     * 生成订单操作幂等键
     *
     * @param orderNo   订单编号
     * @param operation 操作类型
     * @return 幂等键
     */
    public String generateOrderOperationKey(String orderNo, String operation) {
        return "order:operation:" + orderNo + ":" + operation;
    }

    /**
     * 生成请求令牌键
     *
     * @param token 请求令牌
     * @return 幂等键
     */
    public String generateRequestTokenKey(String token) {
        return "request:token:" + token;
    }
}