package com.sharep.domain.follow.presentation.dto.response;

import com.sharep.domain.follow.domain.Follow;
import com.sharep.domain.user.domain.User;
import lombok.Getter;

@Getter
public class FollowingResponse {
    private final Long followerId;
    private final String followerLoginId;
    private final String followerNickname;
    private final Boolean follower;

    public FollowingResponse(Follow follow, Boolean follower) {
        this.followerId = follow.getFollower().getId();
        this.followerLoginId = follow.getFollower().getLoginId();
        this.followerNickname = follow.getFollower().getNickname();
        this.follower = follower;
    }
}
