package com.sharep.global.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    PROMPT_NOT_FOUND(404, "해당 프롬프트 게시글을 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(409, "같은 아이디가 존재합니다."),
    LOGIN_FAILED(401, "아이디 또는 비밀번호가 올바르지 않습니다."),
    FORBIDDEN(403, "권한이 없습니다.");

    private final Integer errorCode;
    private final String message;
}
