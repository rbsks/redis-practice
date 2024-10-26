package org.redis.common.aspect.cache.strategy;

import org.redis.common.aspect.cache.RedisDataStructure;

import java.util.concurrent.TimeUnit;

public record RedisCacheInfo(
        String cacheNames,
        String key,
        String filed,
        Object value,
        RedisDataStructure redisDataStructure,
        long timeout,
        TimeUnit timeUnit,
        Object[] args,
        String[] parameterNames) {

    public static RedisCacheInfo createWithoutFieldAndValue(
            final String cacheNames, final String key,
            final RedisDataStructure redisDataStructure, final  long timeout,
            final TimeUnit timeUnit, final Object[] args, final String[] parameterNames) {
        return new RedisCacheInfo(
                cacheNames, key, null, null,
                redisDataStructure, timeout, timeUnit, args, parameterNames
        );
    }

    public static RedisCacheInfo createWithoutField(
            final String cacheNames, final String key, final Object value,
            final RedisDataStructure redisDataStructure, final  long timeout,
            final TimeUnit timeUnit, final Object[] args, final String[] parameterNames) {
        return new RedisCacheInfo(
                cacheNames, key, null, value,
                redisDataStructure, timeout, timeUnit, args, parameterNames
        );
    }
}
