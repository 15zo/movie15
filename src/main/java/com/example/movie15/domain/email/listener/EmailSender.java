package com.example.movie15.domain.email.listener;

import com.example.movie15.domain.email.entity.EmailMessage;
import com.example.movie15.domain.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailSender {

    private final EmailService emailService;

    // RabbitMQ 큐에서 메시지를 받는 리스너
    @RabbitListener(queues = "emailQueue")
    public void receiveEmailMessage(EmailMessage emailMessage) {
        // 큐에서 받은 이메일 메시지 처리
        emailService.sendEmail(emailMessage.getUserEmail(), emailMessage.getSubject(), emailMessage.getText());
    }
}
