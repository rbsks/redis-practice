package org.redis.common.aspect.cache.strategy;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * TTL을 지원이 미흡한 자료구조에서는 getAndExpire(), setWithTimeout 메서드를 오버라이드 하지 않는다.
 * 또는 redisTemplate execute를 사용해서 명령어를 직접 실행한다.
 */
public interface RedisOperationStrategy {

    <K, V> Object get(RedisTemplate<K, V> restTemplate, K key);

    default <K, V> Object getAndExpire(RedisTemplate<K, V> restTemplate, K key, long timeout, TimeUnit timeUnit) {
        throw new UnsupportedOperationException("getAndExpire is not supported for this data structure.");
    }

    <K, V> void set(RedisTemplate<K, V> restTemplate, K key, V value);

    default <K, V> void setWithTimeout(RedisTemplate<K, V> restTemplate, K key, V value, long timeout, TimeUnit timeUnit) {
        throw new UnsupportedOperationException("setWithTimeout is not supported for this data structure.");
    }
}
