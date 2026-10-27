package com.sharep.domain.prompt.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.user.domain.User;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class PromptAllResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final String tag;
    @JsonProperty("like_count")
    private final Long likeCount;
    @JsonProperty("create_at")
    private final LocalDateTime createAt;
    private final Boolean liked;

    public PromptAllResponse(Prompt prompt, Boolean liked) {
        this.id = prompt.getId();
        this.title = prompt.getTitle();
        this.description = prompt.getDescription();
        this.tag = prompt.getTag();
        this.likeCount = prompt.getLikeCount();
        this.createAt = prompt.getCreateAt();
        this.liked = liked;
    }
}
