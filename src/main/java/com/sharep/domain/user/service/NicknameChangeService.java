package com.sharep.domain.user.service;

import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.domain.user.presentation.dto.request.NicknameChangeRequest;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NicknameChangeService {
    private final UserRepository userRepository;
    private final LoadCurrentUserService loadCurrentUserService;

    @Transactional
    public void execute(User authenticatedUser, NicknameChangeRequest request) {
        User user = loadCurrentUserService.execute(authenticatedUser);
        if (user.getNickname().equals(request.getNickname())) {
            throw new CustomException(ErrorCode.ALREADY_NICKNAME);
        }
        user.changeNickname(request.getNickname());
    }
}
