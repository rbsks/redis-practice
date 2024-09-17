package org.redis.common.aspect.redis;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * TODO 저장, 갱신, 삭제를 하기 위한 RedisOperationStrategy interface를 만든 후 각 자료구조마다 인터페이스를 구현
 */
@Aspect
@Component
public class RedisCacheAspect {

    private final RedisTemplate<String, Object> stringObjectRedisTemplate;

    public RedisCacheAspect(RedisTemplate<String, Object> stringObjectRedisTemplate) {
        this.stringObjectRedisTemplate = stringObjectRedisTemplate;
    }

    @Around(value = "@annotation(org.redis.common.aspect.redis.RedisCacheable)")
    public Object cacheable(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RedisCacheable redisCacheable = signature.getMethod().getAnnotation(RedisCacheable.class);

        validationMetaData(redisCacheable.cacheNames(), redisCacheable.key());

        String cacheKey =
                generateCacheKey(redisCacheable.cacheNames(), getKey(joinPoint.getArgs(), signature.getParameterNames(), redisCacheable.key()));
        TimeUnit timeUnit = redisCacheable.timeUit();
        long timeout = redisCacheable.timeout();

        // 키가 존재하면 TTL refresh
        Object value = stringObjectRedisTemplate.opsForValue().getAndExpire(cacheKey, timeout, timeUnit);
        if (value != null) {
            return value;
        }

        Object methodReturnValue = joinPoint.proceed();

        Boolean result = timeout < 0 ?
                stringObjectRedisTemplate.opsForValue().setIfAbsent(cacheKey, methodReturnValue)
                :
                stringObjectRedisTemplate.opsForValue().setIfAbsent(cacheKey, methodReturnValue, timeout, timeUnit);

        return methodReturnValue;
    }

    @Around(value = "@annotation(org.redis.common.aspect.redis.RedisCachePut)")
    public Object cachePut(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RedisCachePut redisCacheput = signature.getMethod().getAnnotation(RedisCachePut.class);

        validationMetaData(redisCacheput.cacheNames(), redisCacheput.key());

        String cacheKey = 
                generateCacheKey(redisCacheput.cacheNames(), getKey(joinPoint.getArgs(), signature.getParameterNames(), redisCacheput.key()));
        TimeUnit timeUnit = redisCacheput.timeUit();
        long timeout = redisCacheput.timeout();

        Object methodReturnValue = joinPoint.proceed();

        if (timeout < 0) {
            stringObjectRedisTemplate.opsForValue().set(cacheKey, methodReturnValue);
        } else {
            stringObjectRedisTemplate.opsForValue().set(cacheKey, methodReturnValue, timeout, timeUnit);
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

}
