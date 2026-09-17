package com.sharep.domain.user.service;

import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.domain.user.presentation.dto.request.UserSignupRequest;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class UserSignupService {
    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(UserSignupRequest request) {
        if(userRepository.existsByLoginId(request.getLoginId())) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                        .build();

        try {
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            if (isDuplicateKey(e)) {
                throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
            }
            throw e;
        }
    }

    private boolean isDuplicateKey(Throwable exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            // MySQL duplicate-key error; other integrity failures must not become 409.
            if (cause instanceof SQLException sqlException
                    && sqlException.getErrorCode() == 1062) {
                return true;
            }
        }
        return false;
    }
}
