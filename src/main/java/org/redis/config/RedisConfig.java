package org.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        return new LettuceConnectionFactory(
                new RedisStandaloneConfiguration("localhost", 6379)
        );
    }

    @Bean("stringObjectRedisTemplate")
    public RedisTemplate<String, Object> stringRedisTemplate() {
        RedisTemplate<String, Object> stringRedisTemplate = new RedisTemplate<>();
        stringRedisTemplate.setConnectionFactory(lettuceConnectionFactory());
        stringRedisTemplate.setDefaultSerializer(new StringRedisSerializer());

        return stringRedisTemplate;
    }
}
