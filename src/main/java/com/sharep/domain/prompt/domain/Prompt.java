package com.sharep.domain.prompt.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.Date;
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

    private LocalDateTime date;

    @Builder
    public Prompt(String title, String description, String prompt, List<String> tag, Long author, LocalDateTime date) {
        this.title = title;
        this.description = description;
        this.prompt = prompt;
        this.tag = tag;
        this.author = author;
        this.likeCount = 0L;
        this.date = date;
    }
}
