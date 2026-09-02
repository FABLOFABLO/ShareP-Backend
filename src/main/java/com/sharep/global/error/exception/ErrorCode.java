package com.sharep.global.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    PROMPT_NOT_FOUND(404, "해당 프롬프트 게시글을 찾을 수 없습니다."),
    SIGNUP_NOT_FOUND(409, "같은 아이디가 존재합니다.");

    private final Integer errorCode;
    private final String message;
}
