package com.sharep.domain.user.service;

import com.sharep.global.jwt.AccessTokenDetails;
import com.sharep.global.jwt.AccessTokenBlacklist;
import com.sharep.global.jwt.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final RefreshTokenStore refreshTokenStore;
    private final AccessTokenBlacklist accessTokenBlacklist;

    public void logout(String loginId, AccessTokenDetails token) {
        long remainingMillis = token.getExpiresAtMillis() - System.currentTimeMillis();
        refreshTokenStore.delete(loginId);

        accessTokenBlacklist.block(token.getTokenId(), remainingMillis);
    }
}
