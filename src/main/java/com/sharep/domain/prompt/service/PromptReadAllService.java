package com.sharep.domain.prompt.service;

import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.domain.SortBy;
import com.sharep.domain.prompt.domain.repository.PromptRepository;
import com.sharep.domain.prompt.presentation.dto.response.PromptResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromptReadAllService {
    private final PromptRepository promptRepository;

    @Transactional(readOnly = true)
    public List<PromptResponse> execute(SortBy sortBy) {
        List<Prompt> prompt;
        List<PromptResponse> promptResponse;

        switch(sortBy) {
            case POPULARITY:
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
}
