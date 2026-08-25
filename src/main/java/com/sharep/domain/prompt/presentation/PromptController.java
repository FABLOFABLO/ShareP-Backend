package com.sharep.domain.prompt.presentation;

import com.sharep.domain.prompt.presentation.dto.request.Filter;
import com.sharep.domain.prompt.presentation.dto.request.SortBy;
import com.sharep.domain.prompt.presentation.dto.request.PromptRequest;
import com.sharep.domain.prompt.presentation.dto.response.PromptAllResponse;
import com.sharep.domain.prompt.presentation.dto.response.PromptDetailResponse;
import com.sharep.domain.prompt.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/prompt")
public class PromptController {
    private final PromptCreateService promptCreateService;
    private final PromptReadAllService promptReadAllService;
    private final PromptReadDetailService promptReadDetailService;
    private final PromptDeleteService promptDeleteService;
    private final PromptSearchService promptSearchService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void promptCreate(@Valid @RequestBody PromptRequest promptRequest) {
        promptCreateService.execute(promptRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PromptAllResponse> promptReadAll(@RequestParam(value = "sort_by", required = false, defaultValue = "LATEST") SortBy sortBy) {
        return promptReadAllService.execute(sortBy);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PromptDetailResponse promptReadDetail(@PathVariable Long id) {
        return promptReadDetailService.execute(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void promptDelete(@PathVariable Long id) {
        promptDeleteService.execute(id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<PromptAllResponse> promptSearch(
            @RequestParam(value = "search", defaultValue = "") String value,
            @RequestParam(value = "filter", defaultValue = "TITLE") Filter filter
            ) {
        return promptSearchService.execute(value, filter);
    }
}
