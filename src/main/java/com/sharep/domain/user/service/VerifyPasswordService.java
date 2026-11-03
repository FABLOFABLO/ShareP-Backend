package com.sharep.domain.user.service;

import com.sharep.domain.user.domain.User;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VerifyPasswordService {
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(String password, User user) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
