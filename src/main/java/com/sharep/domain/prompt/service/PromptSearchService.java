package com.sharep.domain.prompt.service;

import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.domain.repository.PromptLikeRepository;
import com.sharep.domain.prompt.domain.repository.PromptRepository;
import com.sharep.domain.prompt.presentation.dto.request.Filter;
import com.sharep.domain.prompt.presentation.dto.request.PromptReadRequest;
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
public class PromptSearchService {
    private final PromptRepository promptRepository;
    private final PromptLikeRepository promptLikeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<PromptAllResponse> execute(String value, Filter filter, Long userId) {
        List<Prompt> prompts;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USERID_NOT_FOUND));

        switch (filter) {
            case TAG: prompts = promptRepository.findByTagContainsOrderByCreateAtDesc(value);
                break;
            case DESCRIPTION: prompts = promptRepository.findByDescriptionContainsOrderByCreateAtDesc(value);
                break;
            default: prompts = promptRepository.findByTitleContainsOrderByCreateAtDesc(value);
                break;
        }

        List<PromptAllResponse> promptResponse = prompts.stream()
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
