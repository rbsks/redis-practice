package org.redis.common.aspect.cache;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redis.common.aspect.cache.annotation.RedisCachePut;
import org.redis.common.aspect.cache.annotation.RedisCacheable;
import org.redis.common.aspect.cache.strategy.RedisCacheInfo;
import org.redis.common.aspect.cache.strategy.RedisOperationStrategy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
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

        RedisCacheInfo withoutFieldAndValueInfo = RedisCacheInfo.createWithoutFieldAndValue(
                redisCacheable.cacheNames(), redisCacheable.key(), redisCacheable.dataStructure(),
                redisCacheable.timeout(), redisCacheable.timeUit(),
                joinPoint.getArgs(), signature.getParameterNames()
        );

        Object value;
        long timeout = redisCacheable.timeout();
        RedisOperationStrategy redisOperationStrategy = getStrategy(redisCacheable.dataStructure());
        if (timeout > 0) {
            value = redisOperationStrategy.getAndExpire(stringObjectRedisTemplate, withoutFieldAndValueInfo);
        } else {
            value = redisOperationStrategy.get(stringObjectRedisTemplate, withoutFieldAndValueInfo);
        }

        if (value != null) {
            return value;
        }

        Object methodReturnValue = joinPoint.proceed();

        RedisCacheInfo withoutFieldInfo = RedisCacheInfo.createWithoutField(
                redisCacheable.cacheNames(), redisCacheable.key(), methodReturnValue,
                redisCacheable.dataStructure(), redisCacheable.timeout(), redisCacheable.timeUit(),
                joinPoint.getArgs(), signature.getParameterNames()
        );

        if (timeout > 0) {
            redisOperationStrategy.setWithTimeout(stringObjectRedisTemplate, withoutFieldInfo);
        } else {
            redisOperationStrategy.set(stringObjectRedisTemplate, withoutFieldInfo);
        }

        return methodReturnValue;
    }

    @Around(value = "@annotation(org.redis.common.aspect.cache.annotation.RedisCachePut)")
    public Object cachePut(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RedisCachePut redisCacheput = signature.getMethod().getAnnotation(RedisCachePut.class);

        validationMetaData(redisCacheput.cacheNames(), redisCacheput.key());

        long timeout = redisCacheput.timeout();

        RedisOperationStrategy redisOperationStrategy = getStrategy(redisCacheput.dataStructure());

        Object methodReturnValue = joinPoint.proceed();

        RedisCacheInfo withoutFieldInfo = RedisCacheInfo.createWithoutField(
                redisCacheput.cacheNames(), redisCacheput.key(), methodReturnValue,
                redisCacheput.dataStructure(), redisCacheput.timeout(), redisCacheput.timeUit(),
                joinPoint.getArgs(), signature.getParameterNames()
        );

        if (timeout > 0) {
            redisOperationStrategy.setWithTimeout(stringObjectRedisTemplate, withoutFieldInfo);
        } else {
            redisOperationStrategy.set(stringObjectRedisTemplate, withoutFieldInfo);
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

    private RedisOperationStrategy getStrategy(RedisDataStructure redisDataStructure) {
        return redisOperationStrategyMap.get(redisDataStructure);
    }
}
