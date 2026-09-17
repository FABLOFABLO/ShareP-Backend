package com.sharep.global.logout;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class AccessTokenBlacklist {

    private static final String KEY_PREFIX = "sharep:logout:";

    private final StringRedisTemplate redisTemplate;

    public void block(String tokenId, long remainingMillis) {
        if (remainingMillis <= 0) {
            return;
        }

        redisTemplate.opsForValue().set(
                KEY_PREFIX + tokenId,
                "blocked",
                Duration.ofMillis(remainingMillis)
        );
    }

    public boolean isBlocked(String tokenId) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(KEY_PREFIX + tokenId)
        );
    }
}