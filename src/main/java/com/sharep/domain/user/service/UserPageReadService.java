package com.sharep.domain.user.service;

import com.sharep.domain.follow.domain.repository.FollowRepository;
import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.domain.PromptLike;
import com.sharep.domain.prompt.domain.repository.PromptLikeRepository;
import com.sharep.domain.prompt.domain.repository.PromptRepository;
import com.sharep.domain.prompt.presentation.dto.response.PromptAllResponse;
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

        List<Prompt> prompts = promptRepository
                .findByAuthorOrderByCreateAtDesc(user);

        List<PromptAllResponse> promptResponse = prompts.stream()
                .map(prompt -> {
                    if (promptLikeRepository.findByUserAndPrompt(user, prompt) != null) {
                        return new PromptAllResponse(prompt, true);
                    }
                    else {
                        return new PromptAllResponse(prompt, false);
                    }
                })
                .toList();

        List<PromptLike> likedPrompts = promptLikeRepository
                .findLikedPromptsByUserId(user.getId());

        List<PromptAllResponse> promptLikeResponse = likedPrompts.stream()
                .map(likedPrompt -> {
                    if (promptLikeRepository.findByUserAndPrompt(user, likedPrompt.getPrompt()) != null) {
                        return new PromptAllResponse(likedPrompt.getPrompt(), true);
                    }
                    else {
                        return new PromptAllResponse(likedPrompt.getPrompt(), false);
                    }
                })
                .toList();

        return new MyPageResponse(
                user.getId(),
                user.getNickname(),
                followRepository.countByFollowing_Id(user.getId()),
                followRepository.countByFollower_Id(user.getId()),
                promptResponse,
                promptLikeResponse
        );
    }

    public UserProfileResponse readProfile(Long currentUserId, Long targetUserId) {
        if (targetUserId == null || targetUserId <= 0) {
            throw new CustomException(ErrorCode.INVALID_USER_ID);
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Prompt> prompts = promptRepository
                .findByAuthorOrderByCreateAtDesc(targetUser);

        List<PromptAllResponse> promptResponse = prompts.stream()
                .map(prompt -> {
                    if (promptLikeRepository.findByUserAndPrompt(targetUser, prompt) != null) {
                        return new PromptAllResponse(prompt, true);
                    }
                    else {
                        return new PromptAllResponse(prompt, false);
                    }
                })
                .toList();

        boolean isFollowing = !currentUserId.equals(targetUserId)
                && followRepository.existsByFollower_IdAndFollowing_Id(
                        currentUserId, targetUserId);

        return new UserProfileResponse(
                targetUser.getId(),
                targetUser.getNickname(),
                followRepository.countByFollowing_Id(targetUserId),
                followRepository.countByFollower_Id(targetUserId),
                isFollowing,
                promptResponse
        );
    }
}
