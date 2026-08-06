package com.sharep.domain.prompt.presentation;

import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.presentation.dto.request.PromptRequest;
import com.sharep.domain.prompt.presentation.dto.response.PromptResponse;
import com.sharep.domain.prompt.service.PromptCreateService;
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

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void promptCreate(@RequestBody PromptRequest promptRequest) {
        promptCreateService.execute(promptRequest);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<PromptResponse> promptRead() {
        return promptReadService.execute();
    }
}
