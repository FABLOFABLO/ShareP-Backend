package com.sharep.domain.prompt.presentation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PromptLikeRequest {
    private Long userId;
    private Long promptId;
}
