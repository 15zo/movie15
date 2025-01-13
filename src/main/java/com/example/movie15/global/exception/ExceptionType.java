package com.example.movie15.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionType {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,  "해당 유저의 정보를 찾을 수 없습니다."),
    EXIST_USER(HttpStatus.BAD_REQUEST, "동일한 이메일의 사용자가 존재합니다."),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 잘못되었습니다."),
    FORBIDDEN_ACTION(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    MOVIE_NOT_FOUND(HttpStatus.NOT_FOUND, "영화를 찾을 수 없습니다." ),
    HALL_BAD_REQUEST(HttpStatus.BAD_REQUEST,"잘못된 관람관 요청입니다." ) ,
    HALL_NOT_FOUND(HttpStatus.NOT_FOUND,"찾을 수 없는 관람관입니다." ),
    CINEMA_NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없는 영화관입니다."),
    HALL_OR_CINEMA_NOT_FOUND(HttpStatus.NOT_FOUND,"삭제되었거나 존재하지않습니다." ),
    RUN_TIME_BAD_REQUEST(HttpStatus.BAD_REQUEST, "중복된 시간대입니다."),
    CINEMA_HALL_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지않는 상영관입니다." ),
    RUN_TIME_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지않는 영화상영시간대입니다." );


    private final HttpStatus status;
    private final String message;

    ExceptionType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
