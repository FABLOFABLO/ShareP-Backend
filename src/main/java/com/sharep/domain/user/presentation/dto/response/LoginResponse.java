package com.sharep.domain.user.presentation.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginResponse {

    private final String accessToken;
    private final String refreshToken;
}
