package com.sharep.domain.user.service;

import com.sharep.domain.user.domain.Follow;
import com.sharep.domain.user.domain.repository.FollowRepository;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.domain.user.presentation.dto.response.FollowResponse;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowerReadService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public void execute(Long userId) {
        List<Follow> follow = followRepository.findAllByFollower(userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.FOLLOWER_NOT_FOUND)));

        List<FollowResponse> followResponse = follow.stream()
                .map(FollowResponse::new)
                .toList();
    }
}
