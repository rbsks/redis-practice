package org.redis.list;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redis.config.RedisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>List형은 {@link org.springframework.data.redis.core.ListOperations}을 사용하여 값을 조작한다
 * <p> rightXX 메서드는 leftXX 메서드와 반대로 동작.
 */
@Slf4j
@DataRedisTest
@Import(RedisConfig.class)
public class RedisListTest {

    @Autowired
    public RedisTemplate<String, Object> stringObjectRedisTemplate;

    /**
     * <p> 1. LPUSH key element [element ...]
     * <p> 시간 복잡도: O(N)
     * <p> 2. LPOP key [count]
     * <p> 시간 복잡도: O(N)
     */
    @Test
    public void lpushAndPop() throws Exception {
        final String key = "mylist:1";
        stringObjectRedisTemplate.opsForList().leftPushAll(key, "one", "two");

        List<Object> objects = stringObjectRedisTemplate.opsForList().leftPop(key, 2);

        assertThat(objects).isNotNull();
        assertThat(objects.size()).isEqualTo(2);
        assertThat(objects.get(0)).isEqualTo("two");
        assertThat(objects.get(1)).isEqualTo("one");
    }

    /**
     * <p> LINSERT key BEFORE|AFTER pivot element
     * <p> pivot에 해당하는 값을 기준으로 왼쪽 또는 오르른쪽에 값을 PUSH
     * <p> AFTER 옵션은 rightPush(key, pivot, value) 메서드를 통해 조작
     * <p> 시간 복잡도: O(N)
     */
    @Test
    public void linsert() {
        final String key = "mylist:1";
        stringObjectRedisTemplate.opsForList().leftPushAll(key, "one", "two");
        stringObjectRedisTemplate.opsForList().leftPush(key, "one", "three");

        Long size = stringObjectRedisTemplate.opsForList().size(key);
        List<Object> objects = stringObjectRedisTemplate.opsForList().leftPop(key, size != null ? size : 0L);
        assertThat(objects).isNotNull();
        assertThat(objects.get(1)).isEqualTo("three");
    }

    /**
     * <p> BLPOP key [key ...] timeout
     * <p> 지정한 키가 존재하지 않으면 다른 클라이언트가 키 중 하나에 대해 LPUSH 또는 RPUSH 작업을 수행할 때까지 BLPOP은 연결을 차단한다.
     * 데이터가 존재하면 클라이언트는 차단을 해제하고 키의 이름과 팝된 값을 반환한다.
     * <p> 시간 복잡도: O(N)
     */
    @Test
    public void blpop() throws Exception {
        final String key = "mylist:1";
        stringObjectRedisTemplate.opsForList().leftPop(key, 3000L, TimeUnit.MILLISECONDS);
    }

    /**
     * <p> LRANGE key start stop
     * <p> start = 0, stop = -1은 처음 ~ 끝 데이터를 가져옴
     * <p> 시간 복잡도: O(S+N) S는 처음 또는 끝에서 start 지점까지의 오프셋 값
     */
    @Test
    public void lrange() {
        final String key = "mylist:1";
        stringObjectRedisTemplate.opsForList().leftPushAll(key, "one", "two", "three");

        List<Object> range = stringObjectRedisTemplate.opsForList().range(key, 0, -1);

        assertThat(range).isNotNull();
        assertThat(range.get(0)).isEqualTo("three");
        assertThat(range.get(1)).isEqualTo("two");
        assertThat(range.get(2)).isEqualTo("one");
    }
}