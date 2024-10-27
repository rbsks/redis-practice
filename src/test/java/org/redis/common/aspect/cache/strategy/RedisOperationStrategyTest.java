package org.redis.common.aspect.cache.strategy;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class RedisOperationStrategyTest {

    @Test
    public void typeCheck() {
        RedisOperationStrategy stringRedisOperationStrategy = new StringRedisOperationStrategy();

        String str = "str";
        String str2 = "str2";

        Class<? extends String> aClass = str.getClass();
        log.info("{}", aClass);
        Class<? extends String> aClass1 = str2.getClass();
        log.info("{}", aClass1);
        Assertions.assertThat(str.getClass().isInstance(str2)).isTrue();
    }
}