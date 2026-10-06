package com.sharep.global.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AccessTokenDetails {
    private final String tokenId;
    private final long expiresAtMillis;
}
