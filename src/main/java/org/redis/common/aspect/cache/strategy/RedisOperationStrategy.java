package org.redis.common.aspect.cache.strategy;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * TTL이 없는 Hash, List는 어떻게 추상화하지??
 */
public interface RedisOperationStrategy {

    <K, V> Object get(RedisTemplate<K, V> restTemplate, K key, long timeout, TimeUnit timeUnit);
    <K, V> void set(RedisTemplate<K, V> restTemplate, K key, V value, long timeout, TimeUnit timeUnit);
}
