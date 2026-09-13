package com.sharep.domain.user.presentation.dto.response;

import com.sharep.domain.user.domain.Follow;
import com.sharep.domain.user.domain.User;
import lombok.Getter;

@Getter
public class FollowResponse {
    private final User follower;
    private final User following;

    public FollowResponse(Follow follow) {
        this.follower = follow.getFollower();
        this.following = follow.getFollowing();
    }
}
