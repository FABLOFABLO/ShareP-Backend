package com.sharep.domain.prompt.presentation;

import com.sharep.domain.prompt.presentation.dto.request.SortBy;
import com.sharep.domain.prompt.presentation.dto.request.PromptRequest;
import com.sharep.domain.prompt.presentation.dto.response.PromptResponse;
import com.sharep.domain.prompt.service.PromptCreateService;
import com.sharep.domain.prompt.service.PromptDeleteService;
import com.sharep.domain.prompt.service.PromptReadDetailService;
import com.sharep.domain.prompt.service.PromptReadAllService;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void promptCreate(@RequestBody PromptRequest promptRequest) {
        promptCreateService.execute(promptRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PromptResponse> promptReadAll(@RequestParam(value = "sort_by", required = false) SortBy sortBy) {
        return promptReadAllService.execute(sortBy);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PromptResponse promptReadDetail(@PathVariable Long id) {
        return promptReadDetailService.execute(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void promptDelete(@PathVariable Long id) {
        promptDeleteService.execute(id);
    }
}
