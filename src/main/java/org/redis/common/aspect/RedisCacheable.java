package org.redis.common.aspect;

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
    RedisDataStructure dataStructure(); // Redis Operations 객체 동적으로 가져올 수 있게 고민해보기
    long timeout() default -1L;
    TimeUnit timeUit() default TimeUnit.MILLISECONDS;
}