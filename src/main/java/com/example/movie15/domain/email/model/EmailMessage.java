package com.example.movie15.domain.email.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailMessage {
    private Long bookingId; // Redis 이용 위해서
    private String userEmail;
    private String subject;
    private String text;

    // 생성자
    public EmailMessage(Long bookingId, String userEmail, String subject, String text) {
        this.bookingId = bookingId;
        this.userEmail = userEmail;
        this.subject = subject;
        this.text = text;
    }
}
