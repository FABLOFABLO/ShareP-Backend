package com.sharep.domain.user.service;

import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.presentation.dto.request.TokenReissueRequest;
import com.sharep.domain.user.presentation.dto.response.LoginResponse;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import com.sharep.global.jwt.JwtProperty;
import com.sharep.global.jwt.JwtTokenProvider;
import com.sharep.global.jwt.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenReissueService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperty jwtProperty;
    private final RefreshTokenStore refreshTokenStore;

    public LoginResponse execute(TokenReissueRequest request) {
        String oldRefreshToken = request.getRefreshToken();

        User user = validateRefreshToken(oldRefreshToken);
        String loginId = user.getLoginId();

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user);

        boolean rotated = refreshTokenStore.rotate(
                loginId,
                oldRefreshToken,
                newRefreshToken,
                jwtProperty.getRefreshExp()
        );
        log.info("리프레시 토큰 발급 시도");
        if (!rotated) {
            log.info("리프레시 토큰 발급 실패");
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        log.info("리프레시 토큰 발급 완료");

        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    private User validateRefreshToken(String refreshToken) {
        try {
            return jwtTokenProvider.getUserFromRefreshToken(refreshToken);
        } catch (BadCredentialsException e) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
}
