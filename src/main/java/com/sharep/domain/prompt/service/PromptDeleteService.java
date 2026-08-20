package com.sharep.domain.prompt.service;

import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.domain.repository.PromptRepository;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromptDeleteService {
    private final PromptRepository promptRepository;

    @Transactional
    public void execute(Long id) {
        Prompt prompt = promptRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.PROMPT_NOT_FOUND));
        promptRepository.delete(prompt);
    }
}
