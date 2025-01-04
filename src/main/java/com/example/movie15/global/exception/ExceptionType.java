package com.example.movie15.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionType {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,  "해당 유저의 정보를 찾을 수 없습니다."),
    EXIST_USER(HttpStatus.BAD_REQUEST, "동일한 이메일의 사용자가 존재합니다."),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 잘못되었습니다."),
    FORBIDDEN_ACTION(HttpStatus.FORBIDDEN, "권한이 없습니다.");


    private final HttpStatus status;
    private final String message;

    ExceptionType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
