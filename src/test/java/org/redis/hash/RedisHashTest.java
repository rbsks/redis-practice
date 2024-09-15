package org.redis.hash;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redis.config.RedisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p> Hash형은 {@link org.springframework.data.redis.core.HashOperations}을 사용하여 값을 조작한다
 */
@Slf4j
@DataRedisTest
@Import(RedisConfig.class)
public class RedisHashTest {

    @Autowired
    public RedisTemplate<String, Object> stringObjectRedisTemplate;

    /**
     * <p> 1. HSET key field [field value ...]
     * <p> 시간 복잡도: O(N)
     * <p> 2. HGET key field
     * <p> 시간 복잡도: O(1)
     * <p> 해시 키에 지정한 필드값 가져오기
     */
    @Test
    public void hget() throws Exception {
        final String key = "users";
        final String field = "rbsks147@thinkfree";
        Map<String, String> value =
                Map.of("name", "gyubin", "age", "31", "phone", "01040463138");

        // HSET
        HashOperations<String, String, Map<String, String>> stringObjectObjectHashOperations = stringObjectRedisTemplate.opsForHash();
        stringObjectObjectHashOperations.put(key, field, value);

        // 해시 키에 지정한 필드값 가져오기
        Map<String, String> user = Optional.ofNullable(stringObjectObjectHashOperations.get(key, field))
                .orElseThrow(() -> new IllegalArgumentException("not found user"));

        assertThat(user.get("name")).isEqualTo("gyubin");
        assertThat(user.get("age")).isEqualTo("31");
        assertThat(user.get("phone")).isEqualTo("01040463138");
    }

    /**
     * <p> HGETALL key
     * <p> 해시 키에 해당하는 모든 필드 및 필드 값 가져오기
     * <p> 시간 복잡도: O(N)
     */
    @Test
    public void hgetall() throws Exception {
        final String key = "users";
        final String field = "rbsks147@thinkfree";

        // 해시 키에 해당하는 모든 필드 및 필드 값 가져오기
        HashOperations<String, String, Map<String, String>> opsForHash = stringObjectRedisTemplate.opsForHash();
        Map<String, Map<String, String>> entries = opsForHash.entries(key);
        for (Map.Entry<String, Map<String, String>> entry : entries.entrySet()) {
            Map<String, String> user = entry.getValue();

            assertThat(entry.getKey()).isEqualTo(field);
            assertThat(user.get("name")).isEqualTo("gyubin");
            assertThat(user.get("age")).isEqualTo("31");
            assertThat(user.get("phone")).isEqualTo("01040463138");
        }
    }

    /**
     * <p> HVALS key
     * <p> 해시 키에 해당하는 모든 필드 값 가져오기
     * <p> 시간 복잡도: O(N)
     */
    @Test
    public void hvals() throws Exception {
        final String key = "users";

        // 해시 키에 해당하는 모든 필드 값 가져오기
        HashOperations<String, String, Map<String, String>> opsForHash = stringObjectRedisTemplate.opsForHash();
        List<Map<String, String>> entries = opsForHash.values(key);
        for (Map<String, String> entry : entries) {
            assertThat(entry.get("name")).isEqualTo("gyubin");
            assertThat(entry.get("age")).isEqualTo("31");
            assertThat(entry.get("phone")).isEqualTo("01040463138");
        }
    }

    /**
     * <p> HINCRBY key field increment
     * <p> 해시 키에 해당하는 필드 값을 지정한 정수만큼 증가. 필드의 값이 숫자인 경우에만 사용 가능.
     * <p> 시간 복잡도: O(1)
     */
    @Test
    public void hincrby() throws Exception {
        final String key = "visitors";
        final String field = "rbsks147@gmail.com";
        final long count = 6L;

        stringObjectRedisTemplate.opsForHash().put(key, field, count);
        Long increment = stringObjectRedisTemplate.opsForHash().increment(key, field, 1L);

        assertThat(increment).isEqualTo(7L);
    }
}