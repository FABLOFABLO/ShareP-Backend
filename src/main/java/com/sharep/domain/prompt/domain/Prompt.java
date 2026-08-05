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

    private Long like;

    private String date;

    @Builder
    public Prompt(String title, String description, List<String> tag, Long author, Long like, String date) {
        this.title = title;
        this.description = description;
        this.tag = tag;
        this.author = author;
        this.like = like;
        this.date = date;
    }
}
