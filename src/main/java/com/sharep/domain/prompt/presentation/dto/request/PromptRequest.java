package com.sharep.domain.prompt.presentation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class PromptRequest {
    private String title;
    private String description;
    private String prompt;
    private List<String> tag;
    private Long author;
}
