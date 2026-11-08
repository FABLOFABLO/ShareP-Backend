package com.sharep.domain.prompt.service;

import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.domain.repository.PromptLikeRepository;
import com.sharep.domain.prompt.domain.repository.PromptRepository;
import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromptDeleteService {
    private final PromptRepository promptRepository;
    private final PromptLikeRepository promptLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void execute(Long id, Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USERID_NOT_FOUND));
        Prompt prompt = promptRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.PROMPT_NOT_FOUND));
        if (!prompt.getAuthor().getId().equals(currentUserId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        promptLikeRepository.deleteAllByPrompt(prompt);
        promptRepository.delete(prompt);

        currentUser.PromptDelete();
    }
}
