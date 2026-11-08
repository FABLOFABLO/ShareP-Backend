package com.sharep.domain.prompt.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharep.domain.prompt.domain.Prompt;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class PromptDetailResponse {
    @JsonProperty("prompt_id")
    private final Long promptId;
    private final String title;
    private final String description;
    private final String prompt;
    private final String tag;
    @JsonProperty("author_id")
    private final Long authorId;
    private final String author;
    @JsonProperty("is_author")
    private final Boolean isAuthor;
    private final Boolean followed;
    @JsonProperty("like_count")
    private final Long likeCount;
    @JsonProperty("create_at")
    private final LocalDateTime createAt;
    private final Boolean liked;

    public PromptDetailResponse(Prompt prompt, Boolean isAuthor, Boolean followed, Boolean liked) {
        this.promptId = prompt.getId();
        this.title = prompt.getTitle();
        this.description = prompt.getDescription();
        this.prompt = prompt.getPrompt();
        this.tag = prompt.getTag();
        this.authorId = prompt.getAuthor().getId();
        this.author = prompt.getAuthor().getNickname();
        this.isAuthor = isAuthor;
        this.followed = followed;
        this.likeCount = prompt.getLikeCount();
        this.createAt = prompt.getCreateAt();
        this.liked = liked;
    }
}
