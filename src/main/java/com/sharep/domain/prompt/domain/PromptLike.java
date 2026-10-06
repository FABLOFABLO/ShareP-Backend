package com.sharep.domain.prompt.domain;

import com.sharep.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "tbl_prompt_like",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_prompt_like_pair",
                columnNames = {"user_id", "prompt_id"}
        ),
        indexes = @Index(name = "idx_prompt_like_prompt", columnList = "prompt_id")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromptLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prompt_like_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prompt_id", nullable = false)
    private Prompt prompt;
}
