package com.example.movie15.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;

@Getter
public class CommonResponseBody<T> {


    private final String message;

    @JsonInclude(Include.NON_NULL)
    private final T data;

    public CommonResponseBody(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public CommonResponseBody(String message) {
        this(message, null);
    }
}
