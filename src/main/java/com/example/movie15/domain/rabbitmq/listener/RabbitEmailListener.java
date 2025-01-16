package com.example.movie15.domain.rabbitmq.listener;

import com.example.movie15.domain.email.model.EmailMessage;
import com.example.movie15.domain.rabbitmq.common.RedisKey;
import com.example.movie15.domain.rabbitmq.service.RabbitEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitEmailListener  {

    private final RabbitEmailService emailService;

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Delayed Queue 에서 메시지를 처리
     * @param emailMessage 이메일 메시지
     */
    @RabbitListener(queues = "emailDelayQueue")
    public void receiveEmailMessage(EmailMessage emailMessage) {
        processEmailMessage(emailMessage);
    }

    /**
     * 결제 큐에서 메시지를 처리
     * @param emailMessage 이메일 메시지
     */
    @RabbitListener(queues = "chargeQueue")
    public void chargeEmailMessage(EmailMessage emailMessage) {
        processEmailMessage(emailMessage);
    }

    /**
     * 결제취소 큐에서 메시지를 처리
     * @param emailMessage 이메일 메시지
     */
    @RabbitListener(queues = "cancelQueue")
    public void cancelEmailMessage(EmailMessage emailMessage) {

        long bookingId = emailMessage.getBookingId();

        // Redis 에서 EmailMessage 를 가져옴
        EmailMessage redisEmailMessage = (EmailMessage) redisTemplate.opsForHash().get(RedisKey.REMINDER_KEY, bookingId);

        // EmailMessage 가 null 이 아니면 이메일 그대로 발송.
        // null 이면 발송하지 않고 취소된 메시지라는 로그만 남김.
        if (redisEmailMessage != null) {
            processEmailMessage(emailMessage);
        }
        else {
            log.info("이미 결제취소한 메시지입니다. 이메일을 보내지 않습니다.");
        }
    }

    // 공통메소드로 추출
    private void processEmailMessage(EmailMessage emailMessage) {
        String userEmail = emailMessage.getUserEmail();
        String subject = emailMessage.getSubject();
        String text = emailMessage.getText();

        try {
            log.info("이메일 메시지 수신. 유저이메일 : {}", userEmail);
            emailService.sendEmail(userEmail, subject, text);
        } catch (Exception e) {
            log.error("이메일 메시지 수신 실패. 유저이메일 : {}", userEmail);
        }
    }

}