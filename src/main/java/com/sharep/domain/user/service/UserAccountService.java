package com.sharep.domain.user.service;

import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.domain.user.presentation.dto.request.LoginIdChangeRequest;
import com.sharep.domain.user.presentation.dto.request.NicknameChangeRequest;
import com.sharep.domain.user.presentation.dto.request.PasswordChangeRequest;
import com.sharep.domain.user.presentation.dto.response.LoginIdChangeResponse;
import com.sharep.domain.user.presentation.dto.response.NicknameChangeResponse;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import com.sharep.global.error.MySqlErrors;
import com.sharep.global.jwt.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAccountService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenStore refreshTokenStore;

    public NicknameChangeResponse changeNickname(User authenticatedUser, NicknameChangeRequest request) {
        User user = loadCurrentUser(authenticatedUser);
        if (user.getNickname().equals(request.getNickname())) {
            return new NicknameChangeResponse(user.getNickname());
        }
        user.changeNickname(request.getNickname());
        return new NicknameChangeResponse(user.getNickname());
    }

    public LoginIdChangeResponse changeLoginId(User authenticatedUser, LoginIdChangeRequest request) {
        User user = loadCurrentUser(authenticatedUser);
        verifyPassword(request.getPassword(), user);
        if (user.getLoginId().equals(request.getNewId())) {
            return new LoginIdChangeResponse(user.getLoginId());
        }
        if (userRepository.existsByLoginId(request.getNewId())) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }
        String previousLoginId = user.getLoginId();
        user.changeLoginId(request.getNewId());
        flushLoginIdChange();
        refreshTokenStore.delete(previousLoginId);
        return new LoginIdChangeResponse(user.getLoginId());
    }

    public void changePassword(User authenticatedUser, PasswordChangeRequest request) {
        User user = loadCurrentUser(authenticatedUser);
        verifyPassword(request.getCurrentPassword(), user);
        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.flush();
        refreshTokenStore.delete(user.getLoginId());
    }

    private User loadCurrentUser(User authenticatedUser) {
        User user = userRepository.findByIdForUpdate(authenticatedUser.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        if (!Objects.equals(user.getCredentialStamp(), authenticatedUser.getCredentialStamp())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return user;
    }

    private void verifyPassword(String password, User user) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
    }

    private void flushLoginIdChange() {
        try {
            userRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            if (MySqlErrors.isDuplicateKey(exception)) {
                throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
            }
            throw exception;
        }
    }
}
