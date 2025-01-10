package com.example.movie15.domain.rabbitmq.listener;

import com.example.movie15.domain.email.model.EmailMessage;
import com.example.movie15.domain.rabbitmq.service.RabbitEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitEmailListener {
//
//    private final RabbitEmailService emailService;
//
//    // RabbitMQ 큐에서 메시지를 받는 리스너
//    @RabbitListener(queues = "emailQueue")
//    public void receiveEmailMessage(EmailMessage emailMessage) {
//        // 큐에서 받은 이메일 메시지 처리
//        emailService.sendEmail(emailMessage.getUserEmail(), emailMessage.getSubject(), emailMessage.getText());
//    }
//
//    // 결제 큐에서 메시지를 받는 리스너
//    @RabbitListener(queues = "chargeQueue")
//    public void chargeEmailMessage(EmailMessage emailMessage) {
//        emailService.sendEmail(emailMessage.getUserEmail(), emailMessage.getSubject(), emailMessage.getText());
//    }
//
//    // 결제취소 큐에서 메시지를 받는 리스너
//    @RabbitListener(queues = "cancelQueue")
//    public void cancelEmailMessage(EmailMessage emailMessage) {
//        emailService.sendEmail(emailMessage.getUserEmail(), emailMessage.getSubject(), emailMessage.getText());
//    }
}
