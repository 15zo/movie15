package com.example.movie15.global.exception;

import lombok.Getter;

@Getter
public class WrongAccessException extends CustomException {

     public WrongAccessException(final ExceptionType exceptionType) {
         super(exceptionType);
    }

}
