package com.sharep.domain.prompt.domain.repository;

import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.domain.PromptLike;
import com.sharep.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PromptLikeRepository extends JpaRepository<PromptLike, Long> {
    PromptLike findByUserAndPrompt(User user, Prompt prompt);
    List<PromptLike> findAllByUser(User user);
    List<PromptLike> findLikedPromptsByUserId(Long userId);
}
