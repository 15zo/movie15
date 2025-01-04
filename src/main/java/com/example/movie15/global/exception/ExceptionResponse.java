package com.example.movie15.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class ExceptionResponse {
    private final HttpStatus httpStatus;
    private final int statusCode;
    private final Map<String, String> errors;

    public ExceptionResponse(HttpStatus httpStatus, int statusCode, Map<String, String> errors) {
        this.httpStatus = httpStatus;
        this.statusCode = statusCode;
        this.errors = errors;
    }
}
