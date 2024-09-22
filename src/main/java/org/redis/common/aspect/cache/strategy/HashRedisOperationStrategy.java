package org.redis.common.aspect.cache.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * hash는 다른 자료구조와 다르게 key, value 외에 field가 필요하기 때문에 RedisOperationStrategy를 사용할 수 없음.
 * java의 collection와 동일하게 Map은 추상화를 따로 해야할 듯..
 */
@Slf4j
@Component
public class HashRedisOperationStrategy implements RedisOperationStrategy {

    @Override
    public <K, V> Object get(RedisTemplate<K, V> restTemplate, K key) {
        try {
            HashOperations<K, K, Map<String, String>> hashOperations = restTemplate.opsForHash();
            return null;
        } catch (Exception e) {
            log.error("Failed redis cache lookup", e);
            return null;
        }
    }

    @Override
    public <K, V> Object getAndExpire(RedisTemplate<K, V> restTemplate, K key, long timeout, TimeUnit timeUnit) {
        try {
            HashOperations<K, K, Map<String, String>> hashOperations = restTemplate.opsForHash();
            return null;
        } catch (Exception e) {
            log.error("Failed redis cache lookup", e);
            return null;
        }
    }

    @Override
    public <K, V> void set(RedisTemplate<K, V> restTemplate, K key, V value) {
        try {
            HashOperations<K, K, Map<String, String>> hashOperations = restTemplate.opsForHash();
        } catch (Exception e) {
            log.error("Failed to save redis cache", e);
        }
    }

    @Override
    public <K, V> void setWithTimeout(RedisTemplate<K, V> restTemplate, K key, V value, long timeout, TimeUnit timeUnit) {
        try {
            HashOperations<K, K, Map<String, String>> hashOperations = restTemplate.opsForHash();
        } catch (Exception e) {
            log.error("Failed to save redis cache", e);
        }
    }
}
