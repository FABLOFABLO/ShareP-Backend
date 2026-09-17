package com.sharep.global.refresh;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RefreshTokenStore {

    private static final String KEY_PREFIX = "sharep:refresh:v2:";

    private static final DefaultRedisScript<Long> ROTATE_SCRIPT =
            new DefaultRedisScript<>("""
                    local current = redis.call('GET', KEYS[1])

                    if current ~= ARGV[1] then
                        return 0
                    end

                    redis.call('SET', KEYS[1], ARGV[2], 'PX', ARGV[3])
                    return 1
                    """, Long.class);

    private final StringRedisTemplate redisTemplate;

    public void save(String loginId, String token, long ttlMillis) {
        requirePositiveTtl(ttlMillis);

        redisTemplate.opsForValue().set(
                key(loginId),
                token,
                Duration.ofMillis(ttlMillis)
        );
    }

    public boolean rotate(
            String loginId,
            String oldToken,
            String newToken,
            long ttlMillis
    ) {
        requirePositiveTtl(ttlMillis);

        Long result = redisTemplate.execute(
                ROTATE_SCRIPT,
                List.of(key(loginId)),
                oldToken,
                newToken,
                Long.toString(ttlMillis)
        );

        return Long.valueOf(1L).equals(result);
    }

    public void delete(String loginId) {
        redisTemplate.delete(key(loginId));
    }

    private String key(String loginId) {
        return KEY_PREFIX + loginId;
    }

    private void requirePositiveTtl(long ttlMillis) {
        if (ttlMillis <= 0) {
            throw new IllegalArgumentException("TTL must be positive");
        }
    }
}