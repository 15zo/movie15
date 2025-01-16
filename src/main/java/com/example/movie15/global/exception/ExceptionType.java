package com.example.movie15.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public enum ExceptionType {
    SAME_REVIEW(HttpStatus.NOT_FOUND,  "이미 작성된 리뷰와 같습니다. 수정을 원하면 다시 입력해주세요."),
    ALREADY_REVIEW(HttpStatus.NOT_FOUND,  "이미 해당영화에 리뷰를 작성했습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND,  "리뷰 정보를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,  "해당 유저의 정보를 찾을 수 없습니다."),
    EXIST_USER(HttpStatus.BAD_REQUEST, "동일한 이메일의 사용자가 존재합니다."),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 잘못되었습니다."),
    FORBIDDEN_ACTION(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    MOVIE_NOT_FOUND(HttpStatus.NOT_FOUND, "영화를 찾을 수 없습니다." ),
    BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND, "예약 정보를 찾을 수 없습니다"),
    HALL_BAD_REQUEST(HttpStatus.BAD_REQUEST,"잘못된 관람관 요청입니다." ) ,
    HALL_NOT_FOUND(HttpStatus.NOT_FOUND,"찾을 수 없는 관람관입니다." ),
    CINEMA_NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없는 영화관입니다."),
    HALL_OR_CINEMA_NOT_FOUND(HttpStatus.NOT_FOUND,"삭제되었거나 존재하지않습니다." ),
    INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 문의 사항을 찾을 수 없습니다." ),
    INQUIRY_ALREADY_ANSWERED(HttpStatus.CONFLICT, "이미 답변된 문의는 수정하거나 삭제할 수 없습니다."),
    INQUIRY_NO_CHANGES(HttpStatus.BAD_REQUEST, "변경된 내용이 없습니다."),
    INQUIRY_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 문의 사항에 접근할 권한이 없습니다."),
    NOT_BLANK(HttpStatus.BAD_REQUEST, "값이 비어 있거나 공백일 수 없습니다."),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "유효하지 않은 역할 값입니다."),

    ;

    private final HttpStatus status;
    private final String message;

    ExceptionType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
