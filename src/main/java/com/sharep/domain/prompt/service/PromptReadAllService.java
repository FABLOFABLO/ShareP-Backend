package com.sharep.domain.prompt.service;

import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.domain.repository.PromptLikeRepository;
import com.sharep.domain.prompt.presentation.dto.request.SortBy;
import com.sharep.domain.prompt.domain.repository.PromptRepository;
import com.sharep.domain.prompt.presentation.dto.response.PromptAllResponse;
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
public class PromptReadAllService {
    private final PromptRepository promptRepository;
    private final PromptLikeRepository promptLikeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<PromptAllResponse> execute(SortBy sortBy, Long userId) {
        List<Prompt> prompts;
        List<PromptAllResponse> promptResponse;

        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USERID_NOT_FOUND));

        switch(sortBy) {
            case POPULARITY:
                prompts = promptRepository.findAllByOrderByLikeCountDesc();
                break;
            default:
                prompts = promptRepository.findAllByOrderByCreateAtDesc();
                break;
        }

        promptResponse = prompts.stream()
                .map(prompt -> {
                    Boolean liked;
                    if (promptLikeRepository.findByUserAndPrompt(user, prompt) != null) {
                        return new PromptAllResponse(prompt, true);
                    }
                    else {
                        return new PromptAllResponse(prompt, false);
                    }
                })
                .toList();

        return promptResponse;
    }
}
