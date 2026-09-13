package com.sharep.global.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    PROMPT_NOT_FOUND(404, "해당 프롬프트 게시글을 찾을 수 없습니다.");

    private final Integer errorCode;
    private final String message;
}
