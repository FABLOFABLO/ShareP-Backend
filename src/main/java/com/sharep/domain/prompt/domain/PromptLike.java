package com.sharep.domain.prompt.domain;

import com.sharep.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_prompt_like")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromptLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "prompt_id")
    private Prompt prompt;

    @Builder
    public PromptLike(User user, Prompt prompt) {
        this.user = user;
        this.prompt = prompt;
    }
}
