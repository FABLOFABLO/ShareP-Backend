package com.sharep.domain.prompt.presentation.dto.response;

import com.sharep.domain.prompt.domain.Prompt;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class PromptDetailResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final String prompt;
    private final String tag;
    private final Long author;
    private final Long likeCount;
    private final LocalDateTime createAt;
    private final LocalDateTime nowTime;

    public PromptDetailResponse(Prompt prompt) {
        this.id = prompt.getId();
        this.title = prompt.getTitle();
        this.description = prompt.getDescription();
        this.prompt = prompt.getPrompt();
        this.tag = prompt.getTag();
        this.author = prompt.getAuthor();
        this.likeCount = prompt.getLikeCount();
        this.createAt = prompt.getCreateAt();
        this.nowTime = LocalDateTime.now();
    }
}
