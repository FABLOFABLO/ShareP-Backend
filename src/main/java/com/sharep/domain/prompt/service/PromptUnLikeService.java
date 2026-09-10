package com.sharep.domain.prompt.service;

import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.domain.PromptLike;
import com.sharep.domain.prompt.domain.repository.PromptLikeRepository;
import com.sharep.domain.prompt.domain.repository.PromptRepository;
import com.sharep.domain.prompt.presentation.dto.request.PromptLikeRequest;
import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromptUnLikeService {
    private final PromptLikeRepository promptLikeRepository;
    private final UserRepository userRepository;
    private final PromptRepository promptRepository;

    @Transactional
    public void execute(PromptLikeRequest promptLikeRequest) {
        User user = userRepository.findById(promptLikeRequest.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USERID_NOT_FOUND));
        Prompt prompt = promptRepository.findById(promptLikeRequest.getPromptId())
                .orElseThrow(() -> new CustomException(ErrorCode.PROMPT_NOT_FOUND));

        if (promptLikeRepository.findByUserAndPrompt(user, prompt) != null) {
            throw new CustomException(ErrorCode.ALREADY_UNLIKED);
        }

        PromptLike promptLike = promptLikeRepository.findByUserAndPrompt(user, prompt);

        promptLikeRepository.delete(promptLike);

        prompt.LikeDelete();
    }
}
