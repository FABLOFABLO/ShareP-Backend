package com.sharep.global.refresh;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.concurrent.TimeUnit;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash
public class RefreshToken {

    @Id
    private String accountId;

    @Indexed
    private String token;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long ttl;

    @Builder
    private RefreshToken(String accountId, String token, Long ttl) {
        this.accountId = accountId;
        this.token = token;
        this.ttl = ttl;
    }

    public void updateToken(String token, Long ttl) {
        this.token = token;
        this.ttl = ttl;
    }
}
