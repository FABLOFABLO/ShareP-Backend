package com.sharep.domain.follow.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharep.domain.follow.domain.Follow;
import lombok.Getter;

@Getter
public class FollowingResponse {
    @JsonProperty("follower_id")
    private final Long followerId;
    @JsonProperty("follower_login_id")
    private final String followerLoginId;
    @JsonProperty("follower_nickname")
    private final String followerNickname;
    private final IsFollow following;

    public FollowingResponse(Follow follow, IsFollow following) {
        this.followerId = follow.getFollower().getId();
        this.followerLoginId = follow.getFollower().getLoginId();
        this.followerNickname = follow.getFollower().getNickname();
        this.following = following;
    }
}
