package com.sharep.domain.follow.service;

import com.sharep.domain.follow.domain.Follow;
import com.sharep.domain.follow.domain.repository.FollowRepository;
import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity execute(Long followingId, Long userId) {
        if (userId.equals(followingId)) {
            throw new CustomException(ErrorCode.SAME_PERSON);
        }
        User follower = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USERID_NOT_FOUND));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new CustomException(ErrorCode.USERID_NOT_FOUND));

        if (followRepository.findByFollowerAndFollowing(follower, following) != null) {
            return ResponseEntity.noContent().build();
        }
        
        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        follower.FollowingAdd();
        following.FollowerAdd();

        return ResponseEntity.created(URI.create("")).build();
    }
}
