package org.redis.common.aspect.cache.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class StringRedisOperationStrategy implements RedisOperationStrategy {

    @Override
    public <K, V> Object get(RedisTemplate<K, V> restTemplate, K key, long timeout, TimeUnit timeUnit) {
        try {
            // 키가 존재하면 TTL refresh 후 조회
            return restTemplate.opsForValue().getAndExpire(key, timeout, timeUnit);
        } catch (Exception e) {
            log.error("Failed redis cache lookup", e);
            return null;
        }
    }

    @Override
    public <K, V> void set(RedisTemplate<K, V> restTemplate, K key, V value, long timeout, TimeUnit timeUnit) {
        try {
            if (timeout < 0) {
                restTemplate.opsForValue().set(key, value);
            } else {
                restTemplate.opsForValue().set(key, value, timeout, timeUnit);
            }
        } catch (Exception e) {
            log.error("Failed to save redis cache", e);
        }
    }
}
