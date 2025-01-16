package com.example.movie15.domain.rabbitmq.producer;

import com.example.movie15.domain.booking.entity.Booking;
import com.example.movie15.domain.email.model.EmailMessage;
import com.example.movie15.domain.rabbitmq.common.RedisKey;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitPaymentProducer {

    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final long TEN_MINUTE = 600000;

    /**
     * 결제 완료 시 메시지를 큐에 전송
     *
     * @param booking Booking 객체
     */
    public void sendChargeEvent(Booking booking) {

        Long bookingId = booking.getId();

        String userEmail = booking.getUser().getEmail();

        EmailMessage emailMessage = createEmailMessage(
                bookingId,
                userEmail,
                "영화결제가 완료되었습니다.",
                "영화결제가 완료되었습니다! 시간에 맞춰 입장해주세요!"
        );
        sendQueue("chargeQueue", emailMessage, 0, TEN_MINUTE);
    }

    /**
     * 결제 취소 시 메시지를 큐에 전송
     *
     * @param booking Booking 객체
     */
    public void sendCancelEvent(Booking booking) {

        String userEmail = booking.getUser().getEmail();
        Long bookingId = booking.getId();

        EmailMessage emailMessage = createEmailMessage(
                bookingId,
                userEmail,
                "영화결제가 취소되었습니다.",
                "결제취소가 완료되었습니다!"
        );

        Long deleteRedis = redisTemplate.opsForHash().delete(RedisKey.REMINDER_KEY, bookingId); // redis 에서 삭제

        if (deleteRedis > 0) {
            log.info("예약 메시지가 Redis 에서 성공적으로 취소되었습니다. (예약 ID: {})", bookingId);
        } else {
            log.info("Redis 에서 취소할 메시지를 찾을 수 없습니다. (예약 ID: {})", bookingId);
        }

        sendQueue("cancelQueue", emailMessage, 0, TEN_MINUTE);
    }

    /**
     * 예매시점에 "영화시작 30분 전 이메일 예약."
     *
     * @param booking 영화 예약 정보
     */
    public void movieStartReminderEmail(Booking booking) {

        // 유저 이메일 가져오기
        String userEmail = booking.getUser().getEmail();
        // Booking Id 가져오기
        Long bookingId = booking.getId();

        // 영화 시작 시간 가져오기 (LocalTime)
        LocalTime movieStartTime = booking.getRunTime().getStartTime();

        // 현재 날짜와 결합하여 LocalDateTime 으로 변환 (영화 시작 시간)
        LocalDateTime movieStartDateTime = LocalDate.now().atTime(movieStartTime);

        // 현재 시간과 비교해 딜레이 계산 (ms 단위)
        long delay = ChronoUnit.MILLIS.between(LocalDateTime.now(), movieStartDateTime.minusMinutes(30));
        // 메시지 만료시간
        long expirationTime = ChronoUnit.MILLIS.between(LocalDateTime.now(), movieStartDateTime.plusMinutes(10));

        // 만약 영화 시작이 이미 30분 이내면 경고 로그 남김
        if (delay < 0) {
            log.warn("예약된 영화 시작 시간이 이미 30분 이내이거나 지났습니다. 이메일 예약이 불가능합니다. (영화 시작 시간: {}, 예약자: {})",
                    movieStartTime, userEmail);
            return;
        }

        // 이메일 메시지 생성
        String subject = "곧 영화가 시작합니다!";
        String text = String.format("예매하신 영화가 30분 후에 시작됩니다. 입장을 준비해주세요! 영화 시간: %s", movieStartTime);
        EmailMessage emailMessage = createEmailMessage(bookingId, userEmail, subject, text);

        // Redis 에 저장
        redisTemplate.opsForHash().put(RedisKey.REMINDER_KEY, bookingId, emailMessage); // 메인키 , 식별값 , 데이터

        sendQueue("delayedExchange", emailMessage, delay, expirationTime);
    }

    private void sendQueue(String exchangeName, EmailMessage emailMessage, long delayTime, long expirationTime) {
        String userEmail = emailMessage.getUserEmail();

        try {
            rabbitTemplate.convertAndSend(exchangeName, emailMessage, message -> {
                message.getMessageProperties().setDelayLong(delayTime);
                message.getMessageProperties().setExpiration(String.valueOf(expirationTime));
                return message;
            });
            log.info("RabbitMQ 메시지 전송 성공. (예약자: {})", userEmail);
        } catch (Exception e) {
            log.error("RabbitMQ 메시지 전송 실패. (예약자: {})", userEmail, e);
        }
    }

    private EmailMessage createEmailMessage(long bookingId, String userEmail, String subject, String text) {
        return new EmailMessage(bookingId, userEmail, subject, text);
    }
}