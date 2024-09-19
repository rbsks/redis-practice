package org.redis.user.application;

import org.redis.common.aspect.cache.annotation.RedisCachePut;
import org.redis.common.aspect.cache.annotation.RedisCacheable;
import org.redis.common.aspect.cache.RedisDataStructure;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @RedisCacheable(
            cacheNames = "userIds", key = "userId",
            dataStructure = RedisDataStructure.STRING, timeout = 100000L, timeUit = TimeUnit.MILLISECONDS
    )
    public String findById(final String userId) {
        // db 조회
        return UUID.randomUUID().toString();
    }

    @RedisCachePut(
            cacheNames = "userIds", key = "userId",
            dataStructure = RedisDataStructure.STRING, timeout = 100000L, timeUit = TimeUnit.MILLISECONDS
    )
    public String modifyUser(final String userId) {
        // db 조회
        // 수정
        return UUID.randomUUID().toString();
    }
}
