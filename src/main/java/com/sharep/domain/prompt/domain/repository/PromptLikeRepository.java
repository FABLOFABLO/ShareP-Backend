package com.sharep.domain.prompt.domain.repository;

import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.domain.PromptLike;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PromptLikeRepository extends Repository<PromptLike, Long> {

    @Query("""
            select p
            from PromptLike pl
            join pl.prompt p
            where pl.user.id = :userId
            order by p.createAt desc, p.id desc
            """)
    List<Prompt> findLikedPromptsByUserId(@Param("userId") Long userId);
}
