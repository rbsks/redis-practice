package org.redis.common.aspect.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redis.common.aspect.cache.annotation.RedisCachePut;
import org.redis.common.aspect.cache.annotation.RedisCacheable;
import org.redis.common.aspect.cache.strategy.RedisOperationStrategy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RedisCacheAspect {

    private final RedisTemplate<String, Object> stringObjectRedisTemplate;
    private final Map<RedisDataStructure, RedisOperationStrategy> redisOperationStrategyMap;

    public RedisCacheAspect(
            RedisTemplate<String, Object> stringObjectRedisTemplate,
            Map<RedisDataStructure, RedisOperationStrategy> redisOperationStrategyMap) {
        this.stringObjectRedisTemplate = stringObjectRedisTemplate;
        this.redisOperationStrategyMap = redisOperationStrategyMap;
    }

    @Around(value = "@annotation(org.redis.common.aspect.cache.annotation.RedisCacheable)")
    public Object cacheable(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RedisCacheable redisCacheable = signature.getMethod().getAnnotation(RedisCacheable.class);

        validationMetaData(redisCacheable.cacheNames(), redisCacheable.key());

        String cacheKey =
                generateCacheKey(redisCacheable.cacheNames(), getKey(joinPoint.getArgs(), signature.getParameterNames(), redisCacheable.key()));
        TimeUnit timeUnit = redisCacheable.timeUit();
        long timeout = redisCacheable.timeout();

        RedisOperationStrategy redisOperationStrategy = getStrategy(redisCacheable.dataStructure());

        Object value;
        if (timeout > 0) {
            value = redisOperationStrategy.getAndExpire(stringObjectRedisTemplate, cacheKey, timeout, timeUnit);
        } else {
            value = redisOperationStrategy.get(stringObjectRedisTemplate, cacheKey);
        }

        if (value != null) {
            return value;
        }

        Object methodReturnValue = joinPoint.proceed();

        if (timeout > 0) {
            redisOperationStrategy.setWithTimeout(stringObjectRedisTemplate, cacheKey, methodReturnValue, timeout, timeUnit);
        } else {
            redisOperationStrategy.set(stringObjectRedisTemplate, cacheKey, methodReturnValue);
        }

        return methodReturnValue;
    }

    @Around(value = "@annotation(org.redis.common.aspect.cache.annotation.RedisCachePut)")
    public Object cachePut(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RedisCachePut redisCacheput = signature.getMethod().getAnnotation(RedisCachePut.class);

        validationMetaData(redisCacheput.cacheNames(), redisCacheput.key());

        String cacheKey = 
                generateCacheKey(redisCacheput.cacheNames(), getKey(joinPoint.getArgs(), signature.getParameterNames(), redisCacheput.key()));
        TimeUnit timeUnit = redisCacheput.timeUit();
        long timeout = redisCacheput.timeout();

        RedisOperationStrategy redisOperationStrategy = getStrategy(redisCacheput.dataStructure());

        Object methodReturnValue = joinPoint.proceed();

        if (timeout > 0) {
            redisOperationStrategy.setWithTimeout(stringObjectRedisTemplate, cacheKey, methodReturnValue, timeout, timeUnit);
        } else {
            redisOperationStrategy.set(stringObjectRedisTemplate, cacheKey, methodReturnValue);
        }

        return methodReturnValue;
    }

    private void validationMetaData(String cacheNames, String key) {
        if (!StringUtils.hasText(cacheNames)) {
            throw new NullPointerException("Cache names cannot be null or empty");
        }

        if (!StringUtils.hasText(key)) {
            throw new NullPointerException("Key cannot be null or empty");
        }
    }

    private String getKey(Object[] args, String[] parameterNames, String metaKey) {
        String key;
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(metaKey)) {
                key = (String) args[i];
                return key;
            }
        }

        throw new IllegalArgumentException("Cannot find matching parameter for key: " + metaKey);
    }

    private String generateCacheKey(String cacheName, String key) {
        return String.format("%s::%s", cacheName, key);
    }

    private RedisOperationStrategy getStrategy(RedisDataStructure redisDataStructure) {
        return redisOperationStrategyMap.get(redisDataStructure);
    }
}
