package com.sharep.domain.user.service;

import com.sharep.global.refresh.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void logout(String loginId) {
        refreshTokenRepository.deleteById(loginId);
    }
}
