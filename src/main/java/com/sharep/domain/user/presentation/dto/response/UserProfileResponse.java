package com.sharep.domain.user.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharep.domain.prompt.presentation.dto.response.PromptAllResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class UserProfileResponse {
    @JsonProperty("user_id")
    private final Long userId;
    private final String nickname;
    private final Long follower;
    private final Long following;
    @JsonProperty("followed")
    private final Boolean followed;
    private final List<PromptAllResponse> prompts;
}
