package com.sharep.domain.follow.service;

import com.sharep.domain.follow.domain.Follow;
import com.sharep.domain.follow.domain.repository.FollowRepository;
import com.sharep.domain.follow.presentation.dto.response.FollowingResponse;
import com.sharep.domain.follow.presentation.dto.response.IsFollow;
import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowerReadService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<FollowingResponse> execute(Long userId, User currentUser) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new CustomException(ErrorCode.USERID_NOT_FOUND);
        }

        List<Follow> follows = followRepository.findAllByFollowing(userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.FOLLOWER_NOT_FOUND)));

        List<FollowingResponse> followingResponse = follows.stream()
                .map(follow -> {
                    if (currentUser.getId().equals(follow.getFollower().getId())) {
                        return new FollowingResponse(follow, IsFollow.ME);
                    } else if (followRepository.findByFollowerAndFollowing(currentUser, follow.getFollower()) != null) {
                        return new FollowingResponse(follow, IsFollow.TRUE);
                    } else {
                        return new FollowingResponse(follow, IsFollow.FALSE);
                    }

                })
                .toList();

        return followingResponse;
    }
}
