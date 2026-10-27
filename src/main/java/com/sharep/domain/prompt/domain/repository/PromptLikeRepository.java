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
    @Query("""
            select p
            from PromptLike pl
            join pl.prompt p
            where pl.user.id = :userId
            order by p.createAt desc, p.id desc
            """)
    List<Prompt> findLikedPromptsByUserId(@Param("userId") Long userId);
}
