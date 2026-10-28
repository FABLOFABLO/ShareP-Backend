package com.sharep.domain.user.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.domain.PromptLike;
import com.sharep.domain.prompt.presentation.dto.response.PromptAllResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class MyPageResponse {
    @JsonProperty("user_id")
    private final Long userId;
    private final String nickname;
    private final Long follower;
    private final Long following;
    private final List<PromptAllResponse> prompts;
    @JsonProperty("liked_id")
    private final List<PromptAllResponse> likedPrompts;
}
