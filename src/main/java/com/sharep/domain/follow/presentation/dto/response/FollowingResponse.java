package com.sharep.domain.follow.presentation.dto.response;

import com.sharep.domain.follow.domain.Follow;
import lombok.Getter;

@Getter
public class FollowingResponse {
    private final Long followerId;
    private final String followerLoginId;
    private final String followerNickname;
    private final IsFollow following;

    public FollowingResponse(Follow follow, IsFollow following) {
        this.followerId = follow.getFollower().getId();
        this.followerLoginId = follow.getFollower().getLoginId();
        this.followerNickname = follow.getFollower().getNickname();
        this.following = following;
    }
}
