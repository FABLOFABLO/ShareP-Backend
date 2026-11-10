package com.sharep.domain.user.service;

import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LoadCurrentUserService {
    private final UserRepository userRepository;

    @Transactional
    public User execute(User authenticatedUser) {
        User user = userRepository.findById(authenticatedUser.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        if (!Objects.equals(user.getCredentialStamp(), authenticatedUser.getCredentialStamp())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return user;
    }
}
