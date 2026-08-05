package com.sharep.domain.prompt.domain.repository;

import com.sharep.domain.prompt.domain.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromptRepository extends JpaRepository<Prompt, Long> {
}
