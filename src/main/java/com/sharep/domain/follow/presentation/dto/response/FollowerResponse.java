package com.sharep.domain.follow.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharep.domain.follow.domain.Follow;
import lombok.Getter;

@Getter
public class FollowerResponse {
    @JsonProperty("following_id")
    private final Long followingId;
    @JsonProperty("following_login_id")
    private final String followingLoginId;
    @JsonProperty("following_nickname")
    private final String followingNickname;
    private final IsFollow following;

    public FollowerResponse(Follow follow, IsFollow following) {
        this.followingId = follow.getFollowing().getId();
        this.followingLoginId = follow.getFollowing().getLoginId();
        this.followingNickname = follow.getFollowing().getNickname();
        this.following = following;
    }
}
