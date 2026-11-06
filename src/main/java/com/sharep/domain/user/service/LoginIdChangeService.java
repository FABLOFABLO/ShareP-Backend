package com.sharep.domain.user.service;

import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.domain.user.presentation.dto.request.LoginIdChangeRequest;
import com.sharep.domain.user.presentation.dto.response.LoginResponse;
import com.sharep.global.error.MySqlErrors;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import com.sharep.global.jwt.JwtTokenProvider;
import com.sharep.global.jwt.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginIdChangeService {
    private final UserRepository userRepository;
    private final RefreshTokenStore refreshTokenStore;
    private final LoadCurrentUserService loadCurrentUserService;
    private final VerifyPasswordService verifyPasswordService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse execute(User authenticatedUser, LoginIdChangeRequest request) {
        log.info("로그인 아이디 변경 시도");
        User user = loadCurrentUserService.execute(authenticatedUser);
        verifyPasswordService.execute(request.getPassword(), user);
        if (user.getLoginId().equals(request.getNewId())) {
            throw new CustomException(ErrorCode.ALREADY_LOGINID);
        }
        if (userRepository.existsByLoginId(request.getNewId())) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }
        String previousLoginId = user.getLoginId();
        user.changeLoginId(request.getNewId());
        try {
            userRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            if (MySqlErrors.isDuplicateKey(exception)) {
                throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
            }
            throw exception;
        }
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.createAndStoreRefreshToken(user);

        refreshTokenStore.delete(previousLoginId);

        return new LoginResponse(accessToken, refreshToken);
    }
}
