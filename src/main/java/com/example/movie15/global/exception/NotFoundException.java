package com.example.movie15.global.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends CustomException {

    public NotFoundException(final ExceptionType exceptionType) {
        super(exceptionType);
    }

}
