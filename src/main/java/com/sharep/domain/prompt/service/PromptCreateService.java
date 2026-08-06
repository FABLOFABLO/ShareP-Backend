package com.sharep.domain.prompt.service;

import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.domain.repository.PromptRepository;
import com.sharep.domain.prompt.presentation.dto.request.PromptRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PromptCreateService {
    private final PromptRepository promptRepository;

    @Transactional
    public void execute(PromptRequest promptRequest) {
        Prompt prompt = Prompt.builder()
                .title(promptRequest.getTitle())
                .description(promptRequest.getDescription())
                .prompt(promptRequest.getPrompt())
                .tag(promptRequest.getTag())
                .author(promptRequest.getAuthor())
                .date(LocalDateTime.now())
                .build();

        promptRepository.save(prompt);
    }
}
