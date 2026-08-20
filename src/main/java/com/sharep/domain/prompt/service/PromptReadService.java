package com.sharep.domain.prompt.service;

import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.domain.repository.PromptRepository;
import com.sharep.domain.prompt.presentation.PromptController;
import com.sharep.domain.prompt.presentation.dto.response.PromptResponse;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromptReadService {
    private final PromptRepository promptRepository;

    @Transactional(readOnly = true)
    public List<PromptResponse> execute(PromptController.SortBy sortBy) {
        List<Prompt> prompt;
        List<PromptResponse> promptResponse;

        switch(sortBy) {
            case popularity:
                prompt = promptRepository.findAllByOrderByLikeCountDesc();
                promptResponse = prompt.stream()
                        .map(PromptResponse::new)
                        .toList();
                break;
                default:
                    prompt = promptRepository.findAllByOrderByCreateAtDesc();
                    promptResponse = prompt.stream()
                            .map(PromptResponse::new)
                            .toList();
                break;
        }

        return promptResponse;
    }

    @Transactional(readOnly = true)
    public PromptResponse execute(Long id) {
        Prompt prompt = promptRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.PROMPT_NOT_FOUND));

        return new PromptResponse(prompt);
    }

}
