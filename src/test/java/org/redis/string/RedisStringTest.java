package org.redis.string;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redis.common.configuration.RedisConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * String형은 {@link org.springframework.data.redis.core.ValueOperations}을 사용하여 값을 조작한다
 */
@Slf4j
@DataRedisTest
@Import(RedisConfiguration.class)
public class RedisStringTest {

    @Autowired
    public RedisTemplate<String, Object> stringObjectRedisTemplate;

    /**
     * <p> SET: 키에 값 저장하기
     * <p> GET: 키값 가져오기
     * <p> 시간 복잡도: O(1)
     */
    @Test
    public void get() throws Exception {
        final String key = "foo";
        final String value = "bar";
        stringObjectRedisTemplate.opsForValue().set(key, value);

        assertThat(stringObjectRedisTemplate.opsForValue().get(key)).isEqualTo(value);
    }

    /**
     * <p> SETNX | SET key NX: 키가 없는 경우 값 저장
     * <p> 시간 복잡도: O(1)
     */
    @Test
    public void setnx() throws Exception {
        final String key = "foo111211231123112312" + UUID.randomUUID();
        final String value = "bar123112312";
        Boolean ifAbsent = stringObjectRedisTemplate.opsForValue().setIfAbsent(key, value);

        assertThat(ifAbsent).isTrue();
        assertThat(stringObjectRedisTemplate.opsForValue().get(key)).isEqualTo(value);
    }

    /**
     * <p> SETXX | SET key XX: 키가 있는 경우 값 저장
     * <p> 시간 복잡도: O(1)
     */
    @Test
    public void setxx() throws Exception {
        final String key = "foo12";
        final String value = "bar2112311231";
        Boolean ifPresent = stringObjectRedisTemplate.opsForValue().setIfPresent(key, value);

        assertThat(ifPresent).isTrue();
        assertThat(stringObjectRedisTemplate.opsForValue().get(key)).isEqualTo(value);
    }

    /**
     * <p> SETEX | PSETEX | SET key (EX, PX, EXAT, PXAT): 키값 저장 시 만료시간 설정
     * <p> 시간 복잡도: O(1)
     */
    @Test
    public void setttl() throws Exception {
        final String key = "ttltest";
        final String value = "ttl";
        stringObjectRedisTemplate.opsForValue().set(key, value, 3000L, TimeUnit.MILLISECONDS);

        Thread.sleep(3000L);
        assertThat(stringObjectRedisTemplate.opsForValue().get(key)).isNull();
    }

    /**
     * <p> INCR key
     * <p> 시간 복잡도: O(1)
     */
    @Test
    public void increment() throws Exception {
        final String key = "count-1";
        final String value = "1";
        stringObjectRedisTemplate.opsForValue().set(key, value);

        stringObjectRedisTemplate.opsForValue().increment(key);

    }
}
