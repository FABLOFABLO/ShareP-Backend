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
    private final LikedPromptReadService likedPromptReadService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void promptCreate(@Valid @RequestBody PromptRequest promptRequest,
                             @AuthenticationPrincipal AuthDetails currentUser) {
        promptCreateService.execute(promptRequest, currentUser.getUser().getId());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PromptAllResponse> promptReadAll(@AuthenticationPrincipal AuthDetails currentUser,
                                                 @RequestParam(value = "sort_by", required = false, defaultValue = "LATEST") SortBy sortBy) {
        return promptReadAllService.execute(sortBy, currentUser.getUser().getId());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PromptDetailResponse promptReadDetail(@AuthenticationPrincipal AuthDetails currentUser,
                                                 @PathVariable Long id) {
        return promptReadDetailService.execute(id, currentUser.getUser().getId());
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
            @AuthenticationPrincipal AuthDetails currentUser,
            @RequestParam(value = "search", defaultValue = "") String value,
            @RequestParam(value = "filter", defaultValue = "TITLE") Filter filter
            ) {
        return promptSearchService.execute(value, filter, currentUser.getUser().getId());
    }

    @PostMapping("/{id}/like")
    @ResponseStatus(HttpStatus.OK)
    public void promptLike(@AuthenticationPrincipal AuthDetails currentUser,@PathVariable Long id) {
        promptLikeService.execute(currentUser.getUser().getId(), id);
    }

    @PostMapping("/{id}/unlike")
    @ResponseStatus(HttpStatus.OK)
    public void promptUnLike(@AuthenticationPrincipal AuthDetails currentUser ,@PathVariable Long id) {
        promptUnLikeService.execute(currentUser.getUser().getId(), id);
    }

    @GetMapping("/like")
    @ResponseStatus(HttpStatus.OK)
    public List<PromptAllResponse> promptLikedRead(@AuthenticationPrincipal AuthDetails currentUser) {
        return likedPromptReadService.execute(currentUser.getUser().getId());
    }
}
