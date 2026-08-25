package com.sharep.domain.prompt.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PromptRequest {

    @NotBlank
    @Size(min = 1, max = 90)
    private String title;

    @Size(max = 900)
    private String description = "";

    @NotBlank
    @Size(min = 1, max = 6000)
    private String prompt;

    @NotBlank
    @Size(min = 1, max = 30)
    private String tag;

    private Long author;
}
