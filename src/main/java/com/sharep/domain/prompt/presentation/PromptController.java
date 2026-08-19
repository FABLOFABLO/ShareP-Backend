package com.sharep.domain.prompt.presentation;

import com.sharep.domain.prompt.presentation.dto.request.PromptRequest;
import com.sharep.domain.prompt.presentation.dto.response.PromptResponse;
import com.sharep.domain.prompt.service.PromptCreateService;
import com.sharep.domain.prompt.service.PromptDeleteService;
import com.sharep.domain.prompt.service.PromptReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/prompt")
public class PromptController {
    private final PromptCreateService promptCreateService;
    private final PromptReadService promptReadService;
    private final PromptDeleteService promptDeleteService;
    public enum SortBy {
        latest,
        popularity
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public void promptCreate(@RequestBody PromptRequest promptRequest) {
        promptCreateService.execute(promptRequest);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<PromptResponse> promptRead(@RequestParam(value = "sort_by", required = false) SortBy sortBy) {
        return promptReadService.execute(sortBy == null ?  SortBy.latest : sortBy);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PromptResponse promptRead(@PathVariable Long id) {
        return promptReadService.execute(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void promptDelete(@PathVariable Long id) {
        promptDeleteService.execute(id);
    }
}
