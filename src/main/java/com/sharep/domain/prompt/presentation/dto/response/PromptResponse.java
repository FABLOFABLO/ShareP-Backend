package com.sharep.domain.prompt.presentation.dto.response;

import com.sharep.domain.prompt.domain.Prompt;
import lombok.Getter;
import java.util.List;

@Getter
public class PromptResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final List<String> tag;
    private final Long author;
    private final String date;
    public PromptResponse(Prompt prompt) {
        this.id = prompt.getId();
        this.title = prompt.getTitle();
        this.description = prompt.getDescription();
        this.tag = prompt.getTag();
        this.author = prompt.getAuthor();
        this.date = prompt.getDate();
    }
}
