package com.sharep.domain.user.presentation.response;

import lombok.Getter;

@Getter
public class UserResponse {
    private final String nickname;

    public UserResponse(String nickname) {
        this.nickname = nickname;
    }
}
