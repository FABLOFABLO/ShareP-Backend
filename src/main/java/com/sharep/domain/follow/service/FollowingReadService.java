package com.sharep.domain.follow.service;

import com.sharep.domain.follow.domain.Follow;
import com.sharep.domain.follow.domain.repository.FollowRepository;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.domain.follow.presentation.dto.response.FollowResponse;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowingReadService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<FollowResponse> execute(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            throw new CustomException(ErrorCode.USERID_NOT_FOUND);
        }

        List<Follow> follow = followRepository.findAllByFollowing(userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.FOLLOWING_NOT_FOUND)));

        List<FollowResponse> followResponse = follow.stream()
                .map(FollowResponse::new)
                .toList();

        return followResponse;
    }
}
