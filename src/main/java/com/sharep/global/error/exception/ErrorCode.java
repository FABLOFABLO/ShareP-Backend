package com.sharep.global.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(404, "해당 사용자가 존재하지 않습니다."),
    INVALID_USER_ID(400, "사용자 ID는 양의 정수여야 합니다."),
    PROMPT_NOT_FOUND(404, "해당 프롬프트 게시글을 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(409, "같은 아이디가 존재합니다."),
    INVALID_PASSWORD(401, "현재 비밀번호가 올바르지 않습니다."),
    UNAUTHORIZED(401, "다시 로그인해주세요."),
    LOGIN_FAILED(401, "아이디 또는 비밀번호가 올바르지 않습니다."),
    INVALID_REFRESH_TOKEN(401, "유효하지 않은 리프레시 토큰입니다."),
    FORBIDDEN(403, "권한이 없습니다.");

    private final Integer errorCode;
    private final String message;
}
