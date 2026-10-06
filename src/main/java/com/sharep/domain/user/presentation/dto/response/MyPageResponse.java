package com.sharep.domain.user.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharep.domain.prompt.domain.Prompt;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class MyPageResponse {
    @JsonProperty("user-id")
    private final Long userId;
    private final String nickname;
    private final long follower;
    private final long following;
    private final List<PromptItem> prompts;
    @JsonProperty("liked-id")
    private final List<PromptItem> likedPrompts;

    @Getter
    public static class PromptItem {
        @JsonProperty("prompt-id")
        private final Long promptId;
        private final String title;
        private final String description;
        private final List<String> tag;
        @JsonProperty("like")
        private final Long likeCount;

        public PromptItem(Prompt prompt) {
            this.promptId = prompt.getId();
            this.title = prompt.getTitle();
            this.description = prompt.getDescription();
            this.tag = List.of(prompt.getTag());
            this.likeCount = prompt.getLikeCount();
        }
    }
}
