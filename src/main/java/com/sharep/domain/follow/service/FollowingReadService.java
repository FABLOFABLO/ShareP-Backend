package com.sharep.domain.follow.service;

import com.sharep.domain.follow.domain.Follow;
import com.sharep.domain.follow.domain.repository.FollowRepository;
import com.sharep.domain.follow.presentation.dto.response.FollowerResponse;
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
public class FollowingReadService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<FollowerResponse> execute(Long userId, User currentUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USERID_NOT_FOUND));

        List<Follow> follows = followRepository.findAllByFollower(user);

        List<FollowerResponse> followerResponse = follows.stream()
                .map(follow -> {
                    if (currentUser.getId().equals(follow.getFollowing().getId())) {
                        return new FollowerResponse(follow, IsFollow.ME);
                    }
                    else if (followRepository.findByFollowerAndFollowing(currentUser, follow.getFollowing()) != null) {
                        return new FollowerResponse(follow, IsFollow.TRUE);
                    } else {
                        return new FollowerResponse(follow, IsFollow.FALSE);
                    }
                })
                .toList();

        return followerResponse;
    }
}
