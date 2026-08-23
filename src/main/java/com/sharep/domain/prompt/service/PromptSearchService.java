package com.sharep.domain.prompt.service;

import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.domain.repository.PromptRepository;
import com.sharep.domain.prompt.presentation.dto.request.Filter;
import com.sharep.domain.prompt.presentation.dto.response.PromptAllResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromptSearchService {
    private final PromptRepository promptRepository;

    @Transactional(readOnly = true)
    public List<PromptAllResponse> execute(String value, Filter filter) {
        List<Prompt> prompt;
        switch (filter) {
            case TAG: prompt = promptRepository.findByTagContainsOrderByCreateAtDesc(value);
                break;
            case DESCRIPTION: prompt = promptRepository.findByDescriptionContainsOrderByCreateAtDesc(value);
                break;
            default: prompt = promptRepository.findByTitleContainsOrderByCreateAtDesc(value);
                break;
        }
        List<PromptAllResponse> promptResponse = prompt.stream()
                .map(PromptAllResponse::new)
                .toList();

        return promptResponse;
    }
}
