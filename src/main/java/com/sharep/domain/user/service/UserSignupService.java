package com.sharep.domain.user.service;

import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.domain.user.presentation.dto.request.UserSignupRequest;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSignupService {
    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(UserSignupRequest request) {
        if(userRepository.existsByLoginId(request.getLoginId())) {
            throw new CustomException(ErrorCode.SIGNUP_NOT_FOUND);
        }

        User user = User.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                        .build();

        userRepository.save(user);
    }
}
