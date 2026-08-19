package com.sharep.domain.prompt.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Table(name = "tbl_prompt")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Prompt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String prompt;

    private List<String> tag;

    private Long author;

    @Column(name = "like_count")
    private Long likeCount;

    private LocalDateTime createAt;

    @Builder
    public Prompt(String title, String description, String prompt, List<String> tag, Long author, LocalDateTime createAt) {
        this.title = title;
        this.description = description;
        this.prompt = prompt;
        this.tag = tag;
        this.author = author;
        this.likeCount = 0L;
        this.createAt = createAt;
    }
}
