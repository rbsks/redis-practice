package org.redis.common.aspect.cache.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class StringRedisOperationStrategy implements RedisOperationStrategy {

    @Override
    public <K, V> Object get(RedisTemplate<K, V> redisTemplate, RedisCacheInfo redisCacheInfo) {
        try {
            String key = getKey(redisCacheInfo.args(), redisCacheInfo.parameterNames(), redisCacheInfo.key());
            String cacheKey = generateCacheKey(redisCacheInfo.cacheNames(), key);

            typeCheck(redisTemplate.getKeySerializer().getTargetType(), cacheKey);

            return redisTemplate.opsForValue().get(cacheKey);
        } catch (Exception e) {
            log.error("Failed redis cache lookup", e);
            return null;
        }
    }

    @Override
    public <K, V> Object getAndExpire(RedisTemplate<K, V> redisTemplate, RedisCacheInfo redisCacheInfo) {
        try {
            String key = getKey(redisCacheInfo.args(), redisCacheInfo.parameterNames(), redisCacheInfo.key());
            String cacheKey = generateCacheKey(redisCacheInfo.cacheNames(), key);

            typeCheck(redisTemplate.getKeySerializer().getTargetType(), cacheKey);

            TimeUnit timeUnit = redisCacheInfo.timeUnit();
            long timeout = redisCacheInfo.timeout();

            return redisTemplate.opsForValue().getAndExpire((K) cacheKey, timeout, timeUnit);
        } catch (Exception e) {
            log.error("Failed redis cache lookup", e);
            return null;
        }
    }

    @Override
    public <K, V> void set(RedisTemplate<K, V> redisTemplate, RedisCacheInfo redisCacheInfo) {
        try {
            String key = getKey(redisCacheInfo.args(), redisCacheInfo.parameterNames(), redisCacheInfo.key());
            String cacheKey = generateCacheKey(redisCacheInfo.cacheNames(), key);

            typeCheck(redisTemplate.getKeySerializer().getTargetType(), cacheKey);
            typeCheck(redisTemplate.getValueSerializer().getTargetType(), redisCacheInfo.value());

            redisTemplate.opsForValue().set((K) cacheKey, (V) redisCacheInfo.value());
        } catch (Exception e) {
            log.error("Failed to save redis cache", e);
        }
    }

    @Override
    public <K, V> void setWithTimeout(RedisTemplate<K, V> redisTemplate, RedisCacheInfo redisCacheInfo) {
        try {
            String key = getKey(redisCacheInfo.args(), redisCacheInfo.parameterNames(), redisCacheInfo.key());
            String cacheKey = generateCacheKey(redisCacheInfo.cacheNames(), key);

            typeCheck(redisTemplate.getKeySerializer().getTargetType(), cacheKey);
            typeCheck(redisTemplate.getValueSerializer().getTargetType(), redisCacheInfo.value());

            TimeUnit timeUnit = redisCacheInfo.timeUnit();
            long timeout = redisCacheInfo.timeout();

            redisTemplate.opsForValue().set((K) cacheKey, (V) redisCacheInfo.value(), timeout, timeUnit);
        } catch (Exception e) {
            log.error("Failed to save redis cache", e);
        }
    }
}
