package com.sharep.domain.follow.presentation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FollowRequest {
    private Long followerId;
    private Long followingId;
}
