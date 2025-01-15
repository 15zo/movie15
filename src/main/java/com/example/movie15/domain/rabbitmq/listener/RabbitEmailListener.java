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

        // Redis 에서 EmailMessage 를 가져옴
        EmailMessage redisEmailMessage = (EmailMessage) redisTemplate.opsForHash().get(RedisKey.REMINDER_KEY, emailMessage.getBookingId());

        // EmailMessage 가 null 이 아니면 이메일 그대로 발송.
        // null 이면 발송하지 않고 취소된 메시지라는 로그만 남김.
        if (redisEmailMessage != null) {
            try {
                log.info("결제 취소 이메일 메시지를 수신했습니다. 유저: {}", emailMessage.getUserEmail());
                emailService.sendEmail(emailMessage.getUserEmail(), emailMessage.getSubject(), emailMessage.getText());
            } catch (Exception e) {
                log.error("유저 '{}'의 결제 취소 이메일 메시지 처리에 실패했습니다.", emailMessage.getUserEmail(), e);
            }
        }
        else {
            log.info("이미 결제취소한 메시지입니다. 이메일을 보내지 않습니다. 유저: {}", emailMessage.getUserEmail());
        }
    }

}