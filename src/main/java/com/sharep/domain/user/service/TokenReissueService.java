package com.sharep.domain.user.service;
import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.domain.user.presentation.dto.request.TokenReissueRequest;
import com.sharep.domain.user.presentation.dto.response.LoginResponse;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import com.sharep.global.jwt.JwtProperty;
import com.sharep.global.jwt.JwtTokenProvider;
import com.sharep.global.refresh.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenReissueService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperty jwtProperty;
    private final RefreshTokenStore refreshTokenStore;
    private final UserRepository userRepository;

    public LoginResponse reissue(TokenReissueRequest request) {
        String oldRefreshToken = request.getRefreshToken();

        User user = validateRefreshToken(oldRefreshToken);
        String loginId = user.getLoginId();

        String newAccessToken =
                jwtTokenProvider.generateAccessToken(loginId);

        String newRefreshToken =
                jwtTokenProvider.createRefreshToken(loginId);

        boolean rotated = refreshTokenStore.rotate(
                loginId,
                oldRefreshToken,
                newRefreshToken,
                jwtProperty.getRefreshExp()
        );

        if (!rotated) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    private User validateRefreshToken(String refreshToken) {
        String loginId;

        try {
            loginId = jwtTokenProvider
                    .getLoginIdFromRefreshToken(refreshToken);
        } catch (BadCredentialsException e) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        return userRepository.findByLoginId(loginId)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));
    }
}