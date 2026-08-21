package com.sharep.global.error.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    public final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
