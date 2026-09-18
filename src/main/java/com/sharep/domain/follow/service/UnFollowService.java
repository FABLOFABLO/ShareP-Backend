package com.sharep.domain.follow.service;

import com.sharep.domain.follow.domain.Follow;
import com.sharep.domain.follow.domain.repository.FollowRepository;
import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnFollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public void execute(Long followingId, Long userId) {
        if (userId.equals(followingId)) {
            throw new CustomException(ErrorCode.SAME_PERSON);
        }
        User follower = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USERID_NOT_FOUND));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new CustomException(ErrorCode.USERID_NOT_FOUND));

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following);

        if (follow == null) {
            throw new CustomException(ErrorCode.ALREADY_UNFOLLOWED);
        }

        follower.FollowingDelete();
        following.FollowerDelete();

        followRepository.delete(follow);
    }
}
