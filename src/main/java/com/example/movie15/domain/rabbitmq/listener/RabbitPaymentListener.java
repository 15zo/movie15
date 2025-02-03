package com.example.movie15.domain.rabbitmq.listener;

import com.example.movie15.domain.booking.entity.Booking;
import com.example.movie15.domain.booking.repository.BookingRepository;
import com.example.movie15.domain.email.model.EmailMessage;
import com.example.movie15.domain.rabbitmq.common.QueueBindings;
import com.example.movie15.domain.rabbitmq.common.RedisKey;
import com.example.movie15.domain.rabbitmq.service.RabbitEmailService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitPaymentListener {

    private final BookingRepository bookingRepository;
    private final RabbitEmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * chargeQueue 에서 이메일 메시지를 처리.
     *
     * @param bookingId 예약 ID
     */
    @RabbitListener(queues = QueueBindings.CHARGE_QUEUE, ackMode = "MANUAL")
    public void chargeEmailMessage(Long bookingId, Channel channel, Message message) throws Exception {
        Booking booking = bookingRepository.findBookingWithUser(bookingId);

        try {
            EmailMessage emailMessage = createEmailMessage(
                    bookingId,
                    booking.getUser().getEmail(),
                    "영화결제가 완료되었습니다.",
                    "영화결제가 완료되었습니다! 시간에 맞춰 입장해주세요!"
            );

            processEmailMessage(emailMessage);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.warn("예약성공메시지 오류. DLQ 로 발송 : BOOKING 아이디 : {}", bookingId);

            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);  // 재큐하지 않고 DLQ로 이동
        }
    }

    /**
     * cancelQueue 에서 이메일 메시지를 처리.
     * 결제 취소된 예약에 대해 이메일을 보내기 전에 Redis 에서 해당 예약의 상태를 확인.
     *
     * @param bookingId 예약 ID
     */
    @RabbitListener(queues = QueueBindings.CANCEL_QUEUE, ackMode = "MANUAL")
    public void cancelEmailMessage(Long bookingId, Channel channel, Message message) throws Exception {
        Booking booking = bookingRepository.findBookingWithUser(bookingId);

        try {
            Long deleteRedis = redisTemplate.opsForHash().delete(RedisKey.REMINDER_KEY, bookingId); // redis 에서 삭제

            if (deleteRedis > 0) {
                log.info("예약 메시지가 Redis 에서 성공적으로 취소되었습니다. (예약 ID: {})", bookingId);
            } else {
                log.warn("Redis 에서 취소할 메시지를 찾을 수 없습니다. (예약 ID: {})", bookingId);
            }

            EmailMessage emailMessage = createEmailMessage(
                    bookingId,
                    booking.getUser().getEmail(),
                    "영화결제가 취소되었습니다.",
                    "결제취소가 완료되었습니다!"
            );

            processEmailMessage(emailMessage);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.warn("예약취소메시지오류. DLQ 로 발송. BOOKING 아이디 : {}", bookingId);

            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);  // 재큐하지 않고 DLQ로 이동
        }
    }

    /**
     * Delayed Queue 에서 이메일 메시지를 처리.
     *
     * @param bookingId 예약 ID
     */
    @RabbitListener(queues = QueueBindings.EMAIL_DELAY_QUEUE, ackMode = "MANUAL")
    public void delayEmailMessage(Long bookingId, Channel channel, Message message) throws Exception {
        Booking booking = bookingRepository.findBookingWithUser(bookingId);

        try {
            // 이메일 메시지 생성
            EmailMessage emailMessage = createEmailMessage(
                    bookingId,
                    booking.getUser().getEmail(),
                    "곧 영화가 시작합니다!",
                    String.format("예매하신 영화가 30분 후에 시작됩니다. 입장을 준비해주세요! 영화 시간: %s", booking.getRunTime().getStartTime())
            );

            // Redis 에서 EmailMessage 를 가져옴
            Long redisBookingId = (Long) redisTemplate.opsForHash().get(RedisKey.REMINDER_KEY, bookingId);

            // null 이면 발송하지 않고 취소된 메시지라는 로그만 남김.
            if (redisBookingId != null) {
                processEmailMessage(emailMessage);
            } else {
                log.info("결제가 취소된 메시지입니다. 이메일을 보내지 않습니다.");
            }

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.warn("예약지연메시지오류. DLQ 로 발송. BOOKING 아이디 : {}", bookingId);

            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false, false);  // 재큐하지 않고 DLQ로 이동
        }

    }

    /**
     * 이메일 메시지를 처리하는 공통 메소드.
     * 이메일 메시지를 발송하는 기능을 담당.
     *
     * @param emailMessage 이메일 메시지
     */
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

    /**
     * 이메일 메시지를 생성하는 메소드
     *
     * @param bookingId 예약 ID
     * @param userEmail 이메일 수신자
     * @param subject   이메일 제목
     * @param text      이메일 본문 내용
     * @return 생성된 EmailMessage 객체
     */
    private EmailMessage createEmailMessage(Long bookingId, String userEmail, String subject, String text) {
        return new EmailMessage(bookingId, userEmail, subject, text);
    }

}