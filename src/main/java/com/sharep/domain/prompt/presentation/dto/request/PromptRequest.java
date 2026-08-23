package com.sharep.domain.prompt.presentation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PromptRequest {
    private String title;
    private String description;
    private String prompt;
    private String tag;
    private Long author;
}
