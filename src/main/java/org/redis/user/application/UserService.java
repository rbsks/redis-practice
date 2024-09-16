package org.redis.user.application;

import org.redis.common.aspect.RedisCacheable;
import org.redis.common.aspect.RedisDataStructure;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @RedisCacheable(
            cacheNames = "userIds", key = "userId",
            dataStructure = RedisDataStructure.STRING, timeout = 10000L, timeUit = TimeUnit.MILLISECONDS
    )
    public String findById(final String userId) {
        // db 조회
        return UUID.randomUUID().toString();
    }
}
