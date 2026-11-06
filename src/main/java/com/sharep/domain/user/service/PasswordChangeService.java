package com.sharep.domain.user.service;

import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.domain.user.presentation.dto.request.PasswordChangeRequest;
import com.sharep.domain.user.presentation.dto.response.LoginResponse;
import com.sharep.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordChangeService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoadCurrentUserService loadCurrentUserService;
    private final VerifyPasswordService verifyPasswordService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse execute(User authenticatedUser, PasswordChangeRequest request) {
        log.info("비밀번호 변경 시도");
        User user = loadCurrentUserService.execute(authenticatedUser);
        verifyPasswordService.execute(request.getCurrentPassword(), user);
        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.flush();

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.createAndStoreRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken);
    }
}
