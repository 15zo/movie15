package com.example.movie15.global.exception;

import lombok.Getter;

@Getter
public class ForbiddenException extends CustomException {

    public ForbiddenException(final ExceptionType exceptionType) {super(exceptionType);}
}
