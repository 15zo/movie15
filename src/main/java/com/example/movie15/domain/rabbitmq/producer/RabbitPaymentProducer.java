package com.example.movie15.domain.rabbitmq.producer;

import com.example.movie15.domain.email.model.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitPaymentProducer {

    private final RabbitTemplate rabbitTemplate;
    //private final MovieReservationRepository reservationRepository;

    /**
     * 결제 완료 시 메시지를 큐에 전송
     * @param userEmail 유저 이메일
     */
    public void sendChargeEvent(String userEmail) {
        try {
            EmailMessage emailMessage = new EmailMessage(
                    userEmail,
                    "영화결제가 완료되었습니다.",
                    "영화결제가 완료되었습니다! 시간에 맞춰 입장해주세요!"
            );

            log.info("유저 '{}'에게 'chargeQueue' 로 결제 이메일 메시지를 전송합니다.", userEmail);
            rabbitTemplate.convertAndSend("chargeQueue", emailMessage);
        } catch (Exception e) {
            log.error("유저 '{}'에 대한 결제 이메일 메시지 전송에 실패했습니다.", userEmail, e);
        }
    }

    /**
     * 결제 취소 시 메시지를 큐에 전송
     * @param userEmail 유저 이메일
     */
    public void sendCancelEvent(String userEmail) {
        try {
            EmailMessage emailMessage = new EmailMessage(
                    userEmail,
                    "영화결제가 취소되었습니다.",
                    "결제취소가 완료되었습니다!"
            );

            log.info("유저 '{}'에게 'cancelQueue' 로 결제 취소 이메일 메시지를 전송합니다.", userEmail);
            rabbitTemplate.convertAndSend("cancelQueue", emailMessage);
        } catch (Exception e) {
            log.error("유저 '{}'에 대한 결제 취소 이메일 메시지 전송에 실패했습니다.", userEmail, e);
        }
    }

//    // TODO : 개선필요한 메소드
//    /**
//     * 예매시점에 "영화시작 30분 전 이메일 예약."
//     * @param reservation 영화 예약 정보
//     */
//    public void movieStartReminderEmail(MovieReservation reservation) {
//        try {
//            // 영화 시작 시간 가져오기
//            LocalDateTime movieStartTime = reservation.getMovieTime();
//
//            // 현재 시간과 비교해 딜레이 계산 (ms 단위)
//            long delay = ChronoUnit.MILLIS.between(LocalDateTime.now(), movieStartTime.minusMinutes(30));
//
//            // 만약 영화 시작이 이미 30분 이내면 경고 로그 남김
//            if (delay < 0) {
//                log.warn("예약된 영화 시작 시간이 이미 30분 이내이거나 지났습니다. 이메일 예약이 불가능합니다. (영화 시작 시간: {}, 예약자: {})",
//                        movieStartTime, reservation.getUserEmail());
//                return;
//            }
//
//            // 이메일 메시지 생성
//            String subject = "곧 영화가 시작합니다!";
//            String text = String.format("예매하신 영화가 30분 후에 시작됩니다. 입장을 준비해주세요! 영화 시간: %s", movieStartTime);
//            EmailMessage emailMessage = new EmailMessage(reservation.getUserEmail(), subject, text);
//
//            // Delayed Exchange 를 통해 메시지 예약
//            rabbitTemplate.convertAndSend(
//                    "delayedExchange", // Delayed Exchange 이름
//                    "emailKey",        // Routing Key
//                    emailMessage,
//                    message -> {
//                        message.getMessageProperties().setDelay((int) delay); // 지연 시간 설정 (ms 단위)
//                        return message;
//                    }
//            );
//
//            log.info("영화 시작 30분 전에 이메일 발송을 예약했습니다. (영화 시작 시간: {}, 예약자: {})",
//                    movieStartTime, reservation.getUserEmail());
//        } catch (Exception e) {
//            log.error("영화 시작 알림 이메일 예약에 실패했습니다. (예약자: {})", reservation.getUserEmail(), e);
//        }
//    }
}