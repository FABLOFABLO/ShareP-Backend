package com.sharep.domain.follow.presentation;

import com.sharep.domain.follow.presentation.dto.response.FollowingResponse;
import com.sharep.domain.follow.presentation.dto.response.FollowerResponse;
import com.sharep.domain.follow.service.FollowService;
import com.sharep.domain.follow.service.FollowerReadService;
import com.sharep.domain.follow.service.FollowingReadService;
import com.sharep.domain.follow.service.UnFollowService;
import com.sharep.global.auth.AuthDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {
    private final FollowService followService;
    private final UnFollowService unFollowService;
    private final FollowerReadService followerReadService;
    private final FollowingReadService followingReadService;

    @PostMapping("/{id}/follow")
    @ResponseStatus(HttpStatus.CREATED)
    public void follow(@PathVariable Long id,
                       @AuthenticationPrincipal AuthDetails currentUser) {
        followService.execute(id, currentUser.getUser().getId());
    }

    @DeleteMapping("/{id}/unfollow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unFollow(@PathVariable Long id,
                         @AuthenticationPrincipal AuthDetails currentUser) {
        unFollowService.execute(id, currentUser.getUser().getId());
    }

    @GetMapping("/{id}/follower")
    @ResponseStatus(HttpStatus.OK)
    public List<FollowingResponse> followerRead(@PathVariable Long id,
                                               @AuthenticationPrincipal AuthDetails currentUser) {
        return followerReadService.execute(id, currentUser.getUser());
    }

    @GetMapping("/{id}/following")
    @ResponseStatus(HttpStatus.OK)
    public List<FollowerResponse> followingRead(@PathVariable Long id,
                                                 @AuthenticationPrincipal AuthDetails currentUser) {
        return followingReadService.execute(id, currentUser.getUser());
    }
}
