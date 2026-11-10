package com.sharep.domain.user.service;

import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.domain.PromptLike;
import com.sharep.domain.prompt.domain.repository.PromptLikeRepository;
import com.sharep.domain.prompt.domain.repository.PromptRepository;
import com.sharep.domain.prompt.presentation.dto.response.PromptAllResponse;
import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.domain.user.presentation.dto.response.MyPageResponse;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final PromptRepository promptRepository;
    private final PromptLikeRepository promptLikeRepository;

    @Transactional(readOnly = true)
    public MyPageResponse execute(Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        List<Prompt> prompts = promptRepository
                .findByAuthorOrderByCreateAtDesc(user);

        List<PromptAllResponse> promptResponse = prompts.stream()
                .map(prompt -> {
                    return new PromptAllResponse(prompt, promptLikeRepository.findByUserAndPrompt(user, prompt) != null);
                })
                .toList();

        List<PromptLike> likedPrompts = promptLikeRepository
                .findLikedPromptsByUserId(user.getId());

        List<PromptAllResponse> promptLikeResponse = likedPrompts.stream()
                .map(likedPrompt -> {
                    return new PromptAllResponse(likedPrompt.getPrompt(), true);
                })
                .toList();

        return new MyPageResponse(
                user.getId(),
                user.getNickname(),
                user.getFollowingCount(),
                user.getFollowerCount(),
                user.getPromptCount(),
                promptResponse,
                promptLikeResponse
        );
    }
}
