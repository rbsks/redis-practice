package org.redis.common.aspect.cache.strategy;

import org.springframework.data.redis.core.RedisTemplate;

public interface RedisOperationStrategy {

    <K, V> Object get(RedisTemplate<K, V> redisTemplate, RedisCacheInfo redisCacheInfo);

    <K, V> Object getAndExpire(RedisTemplate<K, V> redisTemplate, RedisCacheInfo redisCacheInfo);

    <K, V> void set(RedisTemplate<K, V> redisTemplate, RedisCacheInfo redisCacheInfo);

    <K, V> void setWithTimeout(RedisTemplate<K, V> redisTemplate, RedisCacheInfo redisCacheInfo);

    default String getKey(Object[] args, String[] parameterNames, String metaKey) {
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(metaKey)) {
                return (String) args[i];
            }
        }

        throw new IllegalArgumentException("Cannot find matching parameter for key: " + metaKey);
    }

    default String generateCacheKey(String cacheName, String key) {
        return String.format("%s::%s", cacheName, key);
    }

    default void typeCheck(Class<?> type, Object target) {
        if (!type.isInstance(target)) {
            throw new ClassCastException("Cannot cast " + target.getClass().getName() + " to " + type.getName());
        }
    }
}
