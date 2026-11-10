package com.sharep.domain.user.service;

import com.sharep.domain.follow.domain.repository.FollowRepository;
import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.domain.repository.PromptLikeRepository;
import com.sharep.domain.prompt.domain.repository.PromptRepository;
import com.sharep.domain.prompt.presentation.dto.response.PromptAllResponse;
import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
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
public class UserProfileReadService {
    private final UserRepository userRepository;
    private final PromptRepository promptRepository;
    private final FollowRepository followRepository;
    private final PromptLikeRepository promptLikeRepository;

    public UserProfileResponse execute(Long currentUserId, Long targetUserId) {
        if (targetUserId == null || targetUserId <= 0) {
            throw new CustomException(ErrorCode.INVALID_USER_ID);
        }
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USERID_NOT_FOUND));

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Prompt> prompts = promptRepository
                .findByAuthorOrderByCreateAtDesc(targetUser);

        List<PromptAllResponse> promptResponse = prompts.stream()
                .map(prompt -> {
                    return new PromptAllResponse(prompt, promptLikeRepository.findByUserAndPrompt(currentUser, prompt) != null);
                })
                .toList();

        return new UserProfileResponse(
                targetUser.getId(),
                targetUser.getNickname(),
                targetUser.getFollowingCount(),
                targetUser.getFollowerCount(),
                targetUser.getPromptCount(),
                followRepository.findByFollowerAndFollowing(currentUser, targetUser) != null,
                promptResponse
        );
    }
}
