package com.sharep.domain.user.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharep.domain.prompt.domain.Prompt;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class UserProfileResponse {
    @JsonProperty("user-id")
    private final Long userId;
    private final String nickname;
    private final long follower;
    private final long following;
    @JsonProperty("is-following")
    private final boolean followingUser;
    private final List<PromptItem> prompts;

    @Getter
    public static class PromptItem {
        @JsonProperty("prompt-id")
        private final Long promptId;
        private final String title;
        private final String description;
        private final String prompt;
        private final List<String> tag;
        @JsonProperty("like")
        private final Long likeCount;

        public PromptItem(Prompt prompt) {
            this.promptId = prompt.getId();
            this.title = prompt.getTitle();
            this.description = prompt.getDescription();
            this.prompt = prompt.getPrompt();
            this.tag = List.of(prompt.getTag());
            this.likeCount = prompt.getLikeCount();
        }
    }
}
