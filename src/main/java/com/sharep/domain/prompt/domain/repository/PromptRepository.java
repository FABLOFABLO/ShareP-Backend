package com.sharep.domain.prompt.domain.repository;

import com.sharep.domain.prompt.domain.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PromptRepository extends JpaRepository<Prompt, Long> {
    List<Prompt> findAllByOrderByCreateAtDesc();
    List<Prompt> findAllByOrderByLikeCountDesc();
}
