package org.redis.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RedisCacheAspect {

    private final RedisTemplate<String, Object> stringObjectRedisTemplate;

    public RedisCacheAspect(RedisTemplate<String, Object> stringObjectRedisTemplate) {
        this.stringObjectRedisTemplate = stringObjectRedisTemplate;
    }

    @Around(value = "@annotation(org.redis.common.aspect.RedisCacheable)")
    public Object cacheableProcess(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RedisCacheable redisCacheable = signature.getMethod().getAnnotation(RedisCacheable.class);

        if (!StringUtils.hasText(redisCacheable.cacheNames())) {
            throw new IllegalArgumentException("Required cache names");
        }

        if (!StringUtils.hasText(redisCacheable.key())) {
            throw new IllegalArgumentException("Required key");
        }

        Object[] args = joinPoint.getArgs();
        String[] parameterNames = signature.getParameterNames();
        String key = null;
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(redisCacheable.key())) {
                key = (String) args[i];
                break;
            }
        }

        if (key == null) {
            throw new IllegalArgumentException("Cannot find matching parameter for key: " + redisCacheable.key());
        }

        String cacheKey = generateKey(redisCacheable.cacheNames(), key);
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

    private String generateKey(String cacheName, String key) {
        return String.format("%s::%s", cacheName, key);
    }

}
