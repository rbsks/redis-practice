package org.redis.common.aspect.cache.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class StringRedisOperationStrategy implements RedisOperationStrategy {

    @Override
    public <K, V> Object get(RedisTemplate<K, V> restTemplate, K key) {
        try {
            return restTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Failed redis cache lookup", e);
            return null;
        }
    }

    @Override
    public <K, V> Object getAndExpire(RedisTemplate<K, V> restTemplate, K key, long timeout, TimeUnit timeUnit) {
        try {
            return restTemplate.opsForValue().getAndExpire(key, timeout, timeUnit);
        } catch (Exception e) {
            log.error("Failed redis cache lookup", e);
            return null;
        }
    }

    @Override
    public <K, V> void set(RedisTemplate<K, V> restTemplate, K key, V value) {
        try {
            restTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("Failed to save redis cache", e);
        }
    }

    @Override
    public <K, V> void setWithTimeout(RedisTemplate<K, V> restTemplate, K key, V value, long timeout, TimeUnit timeUnit) {
        try {
            restTemplate.opsForValue().set(key, value, timeout, timeUnit);
        } catch (Exception e) {
            log.error("Failed to save redis cache", e);
        }
    }
}
