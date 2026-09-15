package com.sharep.domain.follow.presentation;

import com.sharep.domain.follow.presentation.dto.request.FollowRequest;
import com.sharep.domain.follow.presentation.dto.response.FollowResponse;
import com.sharep.domain.follow.service.FollowService;
import com.sharep.domain.follow.service.FollowerReadService;
import com.sharep.domain.follow.service.FollowingReadService;
import com.sharep.domain.follow.service.UnFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/follow")
    @ResponseStatus(HttpStatus.CREATED)
    public void follow(@RequestBody FollowRequest followRequest) {
        followService.execute(followRequest);
    }

    @DeleteMapping("/unfollow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unFollow(@RequestBody FollowRequest followRequest) {
        unFollowService.execute(followRequest);
    }

    @GetMapping("/{id}/follower")
    @ResponseStatus(HttpStatus.OK)
    public List<FollowResponse> followerRead(@PathVariable Long id) {
        return followerReadService.execute(id);
    }

    @GetMapping("/{id}/following")
    @ResponseStatus(HttpStatus.OK)
    public List<FollowResponse> followingRead(@PathVariable Long id) {
        return followingReadService.execute(id);
    }
}
