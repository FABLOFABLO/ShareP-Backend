package com.sharep.domain.prompt.service;

import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.domain.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromptDeleteService {
    private final PromptRepository promptRepository;

    @Transactional
    public void execute(Long id) {
        Prompt prompt = promptRepository.findById(id).orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));
        promptRepository.delete(prompt);
    }
}
