package com.example.movie15.domain.rabbitmq.listener;

import com.example.movie15.domain.email.model.EmailMessage;
import com.example.movie15.domain.rabbitmq.service.RabbitEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitEmailListener {

    private final RabbitEmailService emailService;

    /**
     * Delayed Queue 에서 메시지를 처리
     * @param emailMessage 이메일 메시지
     */
    @RabbitListener(queues = "emailDelayQueue")
    public void receiveEmailMessage(EmailMessage emailMessage) {
        try {
            log.info("지연된 이메일 메시지를 수신했습니다. 유저: {}", emailMessage.getUserEmail());
            emailService.sendEmail(emailMessage.getUserEmail(), emailMessage.getSubject(), emailMessage.getText());
        } catch (Exception e) {
            log.error("유저 '{}'의 지연된 이메일 메시지 처리에 실패했습니다.", emailMessage.getUserEmail(), e);
        }
    }

    /**
     * 결제 큐에서 메시지를 처리
     * @param emailMessage 이메일 메시지
     */
    @RabbitListener(queues = "chargeQueue")
    public void chargeEmailMessage(EmailMessage emailMessage) {
        try {
            log.info("결제 이메일 메시지를 수신했습니다. 유저: {}", emailMessage.getUserEmail());
            emailService.sendEmail(emailMessage.getUserEmail(), emailMessage.getSubject(), emailMessage.getText());
        } catch (Exception e) {
            log.error("유저 '{}'의 결제 이메일 메시지 처리에 실패했습니다.", emailMessage.getUserEmail(), e);
        }
    }

    /**
     * 결제취소 큐에서 메시지를 처리
     * @param emailMessage 이메일 메시지
     */
    @RabbitListener(queues = "cancelQueue")
    public void cancelEmailMessage(EmailMessage emailMessage) {
        try {
            log.info("결제 취소 이메일 메시지를 수신했습니다. 유저: {}", emailMessage.getUserEmail());
            emailService.sendEmail(emailMessage.getUserEmail(), emailMessage.getSubject(), emailMessage.getText());
        } catch (Exception e) {
            log.error("유저 '{}'의 결제 취소 이메일 메시지 처리에 실패했습니다.", emailMessage.getUserEmail(), e);
        }
    }
}