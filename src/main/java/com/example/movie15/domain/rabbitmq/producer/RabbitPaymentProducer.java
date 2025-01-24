package com.example.movie15.domain.rabbitmq.producer;

import com.example.movie15.domain.booking.entity.Booking;
import com.example.movie15.domain.booking.repository.BookingRepository;
import com.example.movie15.domain.rabbitmq.common.QueueBindings;
import com.example.movie15.domain.rabbitmq.common.RedisKey;
import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.NotFoundException;
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

    private final BookingRepository bookingRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final long TEN_MINUTE = 600_000L;

    /**
     * 결제 완료 시 해당 예약에 대한 아이디를 큐에 전송하는 메소드.
     * 만료시간 10분.
     *
     * @param bookingId bookingId
     */
    public void sendChargeEvent(Long bookingId) {
        sendQueue(
                QueueBindings.CHARGE_QUEUE_KEY,
                bookingId
        );
    }

    /**
     * 결제 취소 시 해당 예약에 대한 아이디를 큐에 전송하는 메소드.
     * 만료시간 10분.
     *
     * @param bookingId bookingId
     */
    public void sendCancelEvent(Long bookingId) {
        sendQueue(
                QueueBindings.CANCEL_QUEUE_KEY,
                bookingId
        );
    }

    /**
     * 결제 완료 시 해당 예약에 대한 영화시작시간 30분전에 알림을 보낼수있도록 예약아이디를 큐에 전송하는 메소드.
     *
     * @param bookingId bookingId
     */
    public void movieStartReminderEmail(Long bookingId) {
        Booking booking = bookingRepository.findBookingWithUser(bookingId);

        long delay = calculateMovieStartDelay(booking.getRunTime().getStartTime());

        // 만약 영화 시작이 이미 30분 이내면 경고 로그 남김
        if (delay < 0) {
            log.warn("예약된 영화 시작 시간이 이미 30분 이내이거나 지났습니다. 이메일 예약이 불가능합니다.)");
            return;
        }

        // Redis 에 예약된 알림이 이미 존재하는지 확인. 처음들어가는 정보면 true, 원래 있는 예약값이면 false
        Boolean alreadyExists = redisTemplate.opsForHash().putIfAbsent(RedisKey.REMINDER_KEY, bookingId, bookingId);
        if (!alreadyExists) {
            log.warn("이미 예약된 알림이 존재합니다. 예약을 건너뜁니다. (예약 ID: {})", bookingId);
            return; // 작업 중단
        }

        try {
            rabbitTemplate.convertAndSend(
                    QueueBindings.DELAYED_EXCHANGE,
                    QueueBindings.EMAIL_DELAY_KEY,
                    bookingId,
                    message -> {
                        message.getMessageProperties().setHeader("x-delay", delay); // 큐 대기시간 설정
                        return message;
                    });
            log.info("RabbitMQ 딜레이 메시지 전송 성공. (예약아이디: {})", bookingId);
        } catch (Exception e) {
            log.error("RabbitMQ 메시지 전송 실패. (예약아이디: {})", bookingId, e);
        }
    }

    /**
     * RabbitMQ 큐에 메시지를 전송하는 메소드
     *
     * @param routingKey 큐로 메시지를 전송할 라우팅키
     * @param bookingId  전송할 이메일 메시지
     */
    private void sendQueue(String routingKey, Long bookingId) {
        try {
            rabbitTemplate.convertAndSend(
                    routingKey,
                    bookingId
            );
            log.info("RabbitMQ 메시지 전송 성공. (예약아이디: {})", bookingId);
        } catch (Exception e) {
            log.error("RabbitMQ 메시지 전송 실패. (예약아이디: {})", bookingId, e);
        }
    }

    /**
     * 영화시작 30분전 대기시간을 계산해주는 메소드
     *
     * @param movieStartTime 영화시작시간
     */
    private long calculateMovieStartDelay(LocalTime movieStartTime) {

        // 현재 날짜와 결합하여 LocalDateTime 으로 변환 (영화 시작 시간)
        LocalDateTime movieStartDateTime = LocalDate.now().atTime(movieStartTime);
        LocalDateTime now = LocalDateTime.now();

        // 현재 시간과 비교해 딜레이 계산해서 반환 (ms 단위)
        return ChronoUnit.MILLIS.between(now, movieStartDateTime.minusMinutes(30));
    }
}