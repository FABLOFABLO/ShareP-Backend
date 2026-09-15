package com.sharep.domain.prompt.service;

import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.domain.repository.PromptLikeRepository;
import com.sharep.domain.prompt.domain.repository.PromptRepository;
import com.sharep.domain.prompt.presentation.dto.request.PromptReadRequest;
import com.sharep.domain.prompt.presentation.dto.response.PromptDetailResponse;
import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromptReadDetailService {
    private final PromptRepository promptRepository;
    private final PromptLikeRepository promptLikeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PromptDetailResponse execute(Long id, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USERID_NOT_FOUND));

        Prompt prompt = promptRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.PROMPT_NOT_FOUND));

        if (promptLikeRepository.findByUserAndPrompt(user, prompt) != null) {
            return new PromptDetailResponse(prompt, true);
        }
        else {
            return new PromptDetailResponse(prompt, true);
        }
    }
}
