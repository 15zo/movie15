package com.example.movie15.global.exception;

import lombok.Getter;

@Getter
public class ConflictException extends CustomException {
    public ConflictException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
