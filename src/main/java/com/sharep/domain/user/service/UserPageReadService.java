package com.sharep.domain.user.service;

import com.sharep.domain.follow.domain.repository.FollowRepository;
import com.sharep.domain.prompt.domain.repository.PromptLikeRepository;
import com.sharep.domain.prompt.domain.repository.PromptRepository;
import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.domain.user.presentation.dto.response.MyPageResponse;
import com.sharep.domain.user.presentation.dto.response.UserProfileResponse;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPageReadService {

    private final UserRepository userRepository;
    private final PromptRepository promptRepository;
    private final FollowRepository followRepository;
    private final PromptLikeRepository promptLikeRepository;

    public MyPageResponse readMyPage(Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        List<MyPageResponse.PromptItem> prompts = promptRepository
                .findByAuthorOrderByCreateAtDescIdDesc(user.getId())
                .stream()
                .map(MyPageResponse.PromptItem::new)
                .toList();

        List<MyPageResponse.PromptItem> likedPrompts = promptLikeRepository
                .findLikedPromptsByUserId(user.getId())
                .stream()
                .map(MyPageResponse.PromptItem::new)
                .toList();

        return new MyPageResponse(
                user.getId(),
                user.getNickname(),
                followRepository.countByFollowee_Id(user.getId()),
                followRepository.countByFollower_Id(user.getId()),
                prompts,
                likedPrompts
        );
    }

    public UserProfileResponse readProfile(Long currentUserId, Long targetUserId) {
        if (targetUserId == null || targetUserId <= 0) {
            throw new CustomException(ErrorCode.INVALID_USER_ID);
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<UserProfileResponse.PromptItem> prompts = promptRepository
                .findByAuthorOrderByCreateAtDescIdDesc(targetUserId)
                .stream()
                .map(UserProfileResponse.PromptItem::new)
                .toList();

        boolean isFollowing = !currentUserId.equals(targetUserId)
                && followRepository.existsByFollower_IdAndFollowee_Id(
                        currentUserId, targetUserId);

        return new UserProfileResponse(
                targetUser.getId(),
                targetUser.getNickname(),
                followRepository.countByFollowee_Id(targetUserId),
                followRepository.countByFollower_Id(targetUserId),
                isFollowing,
                prompts
        );
    }
}
