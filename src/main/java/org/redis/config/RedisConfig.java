package org.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
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
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setValueSerializer(new StringRedisSerializer());
        stringRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(new ObjectMapper()));

        return stringRedisTemplate;
    }
}
