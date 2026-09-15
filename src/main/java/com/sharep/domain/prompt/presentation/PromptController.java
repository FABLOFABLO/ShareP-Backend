package com.sharep.domain.prompt.presentation;

import com.sharep.domain.prompt.presentation.dto.request.*;
import com.sharep.domain.prompt.presentation.dto.response.PromptAllResponse;
import com.sharep.domain.prompt.presentation.dto.response.PromptDetailResponse;
import com.sharep.domain.prompt.service.*;
import com.sharep.global.auth.AuthDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final PromptLikeService promptLikeService;
    private final PromptUnLikeService promptUnLikeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void promptCreate(@Valid @RequestBody PromptRequest promptRequest,
                             @AuthenticationPrincipal AuthDetails currentUser) {
        promptCreateService.execute(promptRequest, currentUser.getUser().getId());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PromptAllResponse> promptReadAll(@RequestParam(value = "user-id", defaultValue = "") Long userId, @RequestParam(value = "sort_by", required = false, defaultValue = "LATEST") SortBy sortBy) {
        return promptReadAllService.execute(sortBy, userId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PromptDetailResponse promptReadDetail(@RequestParam(value = "user-id", defaultValue = "") Long userId, @PathVariable Long id) {
        return promptReadDetailService.execute(id, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void promptDelete(@PathVariable Long id,
                             @AuthenticationPrincipal AuthDetails currentUser) {
        promptDeleteService.execute(id, currentUser.getUser().getId());
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<PromptAllResponse> promptSearch(
            @RequestParam(value = "user-id", defaultValue = "") Long userId,
            @RequestParam(value = "search", defaultValue = "") String value,
            @RequestParam(value = "filter", defaultValue = "TITLE") Filter filter
            ) {
        return promptSearchService.execute(value, filter, userId);
    }

    @PostMapping("/like")
    @ResponseStatus(HttpStatus.OK)
    public void promptLike(@RequestBody PromptLikeRequest promptLikeRequest) {
        promptLikeService.execute(promptLikeRequest);
    }

    @PostMapping("/unlike")
    @ResponseStatus(HttpStatus.OK)
    public void promptUnLike(@RequestBody PromptLikeRequest promptLikeRequest) {
        promptUnLikeService.execute(promptLikeRequest);
    }
}
