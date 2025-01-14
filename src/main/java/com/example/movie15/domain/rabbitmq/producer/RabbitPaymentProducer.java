package com.example.movie15.domain.rabbitmq.producer;

import com.example.movie15.domain.email.model.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

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

    // TODO : 개선필요한 메소드
    /**
     * 영화 시작 30분 전 예약자에게 알림 이메일 전송
     */
//    public void sendMovieStartReminderEmails() {
//        try {
//            LocalDateTime now = LocalDateTime.now();
//            LocalDateTime targetTime = now.plusMinutes(30); // 현재로부터 30분 후
//
//            // 30분 내에 시작하는 영화 예약 데이터를 DB 에서 조회
//            List<MovieReservation> reservations = reservationRepository.findByMovieTimeBetween(now, targetTime);
//
//            log.info("30분 이내에 시작하는 영화 예약 {}건을 찾았습니다.", reservations.size());
//
//            // 각 예약자에게 알림 메시지를 생성하고 Delayed Exchange 로 전송
//            for (MovieReservation reservation : reservations) {
//                String subject = "영화 예매 알림";
//                String text = String.format("예매하신 영화가 30분 후에 시작됩니다. 영화 시간: %s", reservation.getMovieTime());
//                EmailMessage emailMessage = new EmailMessage(reservation.getUserEmail(), subject, text);
//
//                log.info("영화가 {}에 시작되는 예약자 '{}'에게 알림 이메일을 전송합니다.", reservation.getMovieTime(), reservation.getUserEmail());
//                rabbitTemplate.convertAndSend(
//                        "delayedExchange", // Delayed Exchange 이름
//                        "emailKey",        // Routing Key
//                        emailMessage,
//                        message -> {
//                            message.getMessageProperties().setDelay(30 * 60 * 1000); // 30분 후 메시지 전달
//                            return message;
//                        }
//                );
//            }
//        } catch (Exception e) {
//            log.error("영화 시작 알림 이메일 전송에 실패했습니다.", e);
//        }
//    }
}