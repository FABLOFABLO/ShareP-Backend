package com.sharep.domain.follow.presentation.dto.response;

import com.sharep.domain.follow.domain.Follow;
import com.sharep.domain.user.domain.User;
import lombok.Getter;

@Getter
public class FollowerResponse {
    private final Long followingId;
    private final String followingLoginId;
    private final String followingNickname;
    private final IsFollow following;

    public FollowerResponse(Follow follow, IsFollow following) {
        this.followingId = follow.getFollowing().getId();
        this.followingLoginId = follow.getFollowing().getLoginId();
        this.followingNickname = follow.getFollowing().getNickname();
        this.following = following;
    }
}
