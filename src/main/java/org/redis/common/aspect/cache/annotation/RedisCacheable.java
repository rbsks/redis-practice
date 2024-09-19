package org.redis.common.aspect.cache.annotation;

import org.redis.common.aspect.cache.RedisDataStructure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCacheable {

    String cacheNames();
    String key();
    RedisDataStructure dataStructure();
    long timeout() default -1L;
    TimeUnit timeUit() default TimeUnit.MILLISECONDS;
}