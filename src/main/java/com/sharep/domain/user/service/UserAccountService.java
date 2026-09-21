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
import com.sharep.global.refresh.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
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
        if (user.getNickname().equals(request.nickname())) {
            return new NicknameChangeResponse(user.getNickname());
        }
        if (User.DEFAULT_NICKNAME.equals(request.nickname())
                || userRepository.existsByNicknameAndIdNot(request.nickname(), user.getId())) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
        user.changeNickname(request.nickname());
        flush(ErrorCode.NICKNAME_ALREADY_EXISTS);
        return new NicknameChangeResponse(user.getNickname());
    }

    public LoginIdChangeResponse changeLoginId(User authenticatedUser, LoginIdChangeRequest request) {
        User user = loadCurrentUser(authenticatedUser);
        verifyPassword(request.password(), user);
        if (user.getLoginId().equals(request.newId())) {
            return new LoginIdChangeResponse(user.getLoginId());
        }
        if (userRepository.existsByLoginIdAndIdNot(request.newId(), user.getId())) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }
        String previousLoginId = user.getLoginId();
        user.changeLoginId(request.newId());
        flush(ErrorCode.USER_ALREADY_EXISTS);
        refreshTokenStore.delete(previousLoginId);
        return new LoginIdChangeResponse(user.getLoginId());
    }

    public void changePassword(User authenticatedUser, PasswordChangeRequest request) {
        User user = loadCurrentUser(authenticatedUser);
        verifyPassword(request.currentPassword(), user);
        user.changePassword(passwordEncoder.encode(request.newPassword()));
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

    private void flush(ErrorCode duplicateError) {
        try {
            userRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
                if (cause instanceof SQLException sqlException && sqlException.getErrorCode() == 1062) {
                    throw new CustomException(duplicateError);
                }
            }
            throw exception;
        }
    }
}
