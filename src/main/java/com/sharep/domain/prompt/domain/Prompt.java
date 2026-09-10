package com.sharep.domain.prompt.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_prompt")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Prompt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prompt_id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false, length = 90)
    private String title;

    @Column(name = "description", length = 900)
    private String description;

    @Column(name = "prompt", nullable = false, length =  6000)
    private String prompt;

    @Column(name = "tag", nullable = false, length = 30)
    private String tag;

    @Column(name = "user_id", nullable = false)
    private Long author;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Builder
    private Prompt(String title, String description, String prompt, String tag, Long author, LocalDateTime createAt) {
        this.title = title;
        this.description = description;
        this.prompt = prompt;
        this.tag = tag;
        this.author = author;
        this.likeCount = 0L;
        this.createAt = createAt;
    }

    public void LikeAdd() {
        this.likeCount++;
    }

    public void LikeDelete() {
        this.likeCount--;
    }
}
