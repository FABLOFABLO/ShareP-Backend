package com.sharep.domain.prompt.service;

import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.domain.repository.PromptRepository;
import com.sharep.domain.prompt.presentation.PromptController;
import com.sharep.domain.prompt.presentation.dto.response.PromptResponse;
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
        List<Prompt> prompt = promptRepository.findAll();

        List<PromptResponse> promptResponse;

        switch(sortBy) {
            case popularity: promptResponse = prompt.stream()
                        .sorted((l1, l2) -> l2.getLikeCount().compareTo(l1.getLikeCount()))
                        .map(PromptResponse::new)
                        .toList();
                break;
            default: promptResponse = prompt.stream()
                    .sorted((l1, l2) -> l2.getDate().compareTo(l1.getDate()))
                    .map(PromptResponse::new)
                    .toList();
                break;
        }

        return promptResponse;
    }

    @Transactional(readOnly = true)
    public PromptResponse execute(Long id) {
        Prompt prompt = promptRepository.findById(id).orElseThrow(() -> new RuntimeException("해당 프롬프트 게시판을 찾을 수 없습니다"));

        return new PromptResponse(prompt);
    }

}
