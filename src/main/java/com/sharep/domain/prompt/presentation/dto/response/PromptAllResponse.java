package com.sharep.domain.prompt.presentation.dto.response;

import com.sharep.domain.prompt.domain.Prompt;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class PromptAllResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final String tag;
    private final Long author;
    private final Long likeCount;
    private final LocalDateTime createAt;
    private final LocalDateTime nowTime;

    public PromptAllResponse(Prompt prompt) {
        this.id = prompt.getId();
        if (prompt.getTitle().length() >= 20) {
            this.title = prompt.getTitle().substring(0, 20) + "...";
        } else {
            this.title = prompt.getTitle();
        }
        if (prompt.getDescription().length() >= 100) {
            this.description = prompt.getDescription().substring(0, 100) + "...";
        } else {
            this.description = prompt.getDescription();
        }
        this.tag = prompt.getTag();
        this.author = prompt.getAuthor();
        this.likeCount = prompt.getLikeCount();
        this.createAt = prompt.getCreateAt();
        this.nowTime = LocalDateTime.now();
    }
}
