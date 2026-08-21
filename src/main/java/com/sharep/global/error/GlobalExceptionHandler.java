package com.sharep.global.error;

import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handlePromptNotFoundException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        Integer code = errorCode.getErrorCode();
        String message = errorCode.getMessage();
        log.error(message, e);
        return new ResponseEntity<>(ErrorResponse.res(code, message), HttpStatus.NOT_FOUND);
    }
}
