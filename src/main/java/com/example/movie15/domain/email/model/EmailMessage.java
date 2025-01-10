package com.example.movie15.domain.email.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailMessage {
    private String userEmail;
    private String subject;
    private String text;

    // 생성자
    public EmailMessage(String userEmail, String subject, String text) {
        this.userEmail = userEmail;
        this.subject = subject;
        this.text = text;
    }
}
