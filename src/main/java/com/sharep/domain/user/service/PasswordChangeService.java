package com.sharep.domain.user.service;

import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.domain.user.presentation.dto.request.PasswordChangeRequest;
import com.sharep.global.jwt.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordChangeService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoadCurrentUserService loadCurrentUserService;
    private final VerifyPasswordService verifyPasswordService;
    private final RefreshTokenStore refreshTokenStore;

    @Transactional
    public void execute(User authenticatedUser, PasswordChangeRequest request) {
        User user = loadCurrentUserService.execute(authenticatedUser);
        verifyPasswordService.execute(request.getCurrentPassword(), user);
        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.flush();
        refreshTokenStore.delete(user.getLoginId());
    }
}
