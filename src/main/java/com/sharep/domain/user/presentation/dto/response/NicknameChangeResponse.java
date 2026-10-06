package com.sharep.domain.user.presentation.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NicknameChangeResponse {
    private final String nickname;
}
