package com.example.movie15.global.exception;

import lombok.Getter;

@Getter
public abstract class CustomException extends RuntimeException {
    private final ExceptionType exceptionType;

    public CustomException(ExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }
}
