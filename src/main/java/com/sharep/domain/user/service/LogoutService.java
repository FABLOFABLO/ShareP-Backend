package com.sharep.domain.user.service;

import com.sharep.global.jwt.JwtTokenFilter;
import com.sharep.global.jwt.JwtTokenProvider;
import com.sharep.global.logout.AccessTokenBlacklist;
import com.sharep.global.refresh.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final RefreshTokenStore refreshTokenStore;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccessTokenBlacklist accessTokenBlacklist;

    public void logout(String loginId, String accessToken) {
        String tokenId =
                jwtTokenProvider.getAccessTokenId(accessToken);

        long remainingMillis =
                jwtTokenProvider.getAccessTokenRemainingMillis(accessToken);

        refreshTokenStore.delete(loginId);

        accessTokenBlacklist.block(tokenId, remainingMillis);
    }
}