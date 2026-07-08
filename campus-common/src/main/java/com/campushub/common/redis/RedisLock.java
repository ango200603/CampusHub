package com.campushub.common.redis;

import com.campushub.common.exception.BusinessException;
import com.campushub.common.exception.ErrorCode;
import java.time.Duration;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

/**
 * Redis based lock helper.
 */
@Component
@RequiredArgsConstructor
public class RedisLock {
    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "return redis.call('del', KEYS[1]) " +
            "else return 0 end";

    private final StringRedisTemplate redisTemplate;

    /**
     * Executes a supplier while holding a Redis lock.
     *
     * @param key lock key
     * @param ttl lock TTL
     * @param supplier protected supplier
     * @param <T> return type
     * @return supplier result
     */
    public <T> T execute(String key, Duration ttl, Supplier<T> supplier) {
        String value = UUID.randomUUID().toString();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(key, value, ttl);
        if (!Boolean.TRUE.equals(locked)) {
            throw new BusinessException(ErrorCode.CONFLICT, "资源正在处理中，请稍后再试");
        }
        try {
            return supplier.get();
        } finally {
            unlock(key, value);
        }
    }

    /**
     * Releases a lock only when the value matches.
     *
     * @param key lock key
     * @param value lock value
     */
    public void unlock(String key, String value) {
        redisTemplate.execute(new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class), Collections.singletonList(key), value);
    }
}
