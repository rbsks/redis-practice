package org.redis.common.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.redis.common.aspect.cache.RedisDataStructure;
import org.redis.common.aspect.cache.strategy.RedisOperationStrategy;
import org.redis.common.aspect.cache.strategy.StringRedisOperationStrategy;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

@Configuration
public class RedisConfiguration {

    private final ApplicationContext applicationContext;

    public RedisConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        return new LettuceConnectionFactory(
                new RedisStandaloneConfiguration("localhost", 6379)
        );
    }

    @Bean
    public CacheManager redisCacheManager() {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMillis(60000L)) // 전역으로 사용할 기본 TTL
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

        // 캐시 이름으로 TTL 관리
        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(
                "userIds", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMillis(10000L)),
                "userNames", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMillis(30000L)),
                "userFeeds", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMillis(30000L))
        );

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(lettuceConnectionFactory())
                .cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    @Bean("stringObjectRedisTemplate")
    public RedisTemplate<String, Object> stringRedisTemplate() {
        RedisTemplate<String, Object> stringRedisTemplate = new RedisTemplate<>();
        stringRedisTemplate.setConnectionFactory(lettuceConnectionFactory());
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setValueSerializer(new StringRedisSerializer());
        stringRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(new ObjectMapper()));

        return stringRedisTemplate;
    }

    @Bean
    public Map<RedisDataStructure, RedisOperationStrategy> redisOperationStrategyMap() {
        return Map.of(
                RedisDataStructure.STRING, applicationContext.getBean("stringRedisOperationStrategy", StringRedisOperationStrategy.class)
        );
    }
}
