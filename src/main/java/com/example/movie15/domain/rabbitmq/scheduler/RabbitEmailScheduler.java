package com.example.movie15.domain.rabbitmq.scheduler;

import lombok.RequiredArgsConstructor;
import org.joda.time.LocalDateTime;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitEmailScheduler {
//
//    //private final MovieReservationRepository reservationRepository;
//    private final RabbitTemplate rabbitTemplate;  // RabbitTemplate 을 사용하여 메시지를 보냄
//
//    // 매 5분마다 실행
//    @Scheduled(fixedRate = 300000)
//    public void sendMovieStartReminderEmails() {
//        LocalDateTime now = LocalDateTime.now(); // 현재시간
//        LocalDateTime targetTime = now.plusMinutes(30);  // 영화 시작 30분 전
//
//        //List<MovieReservation> reservations = reservationRepository.findByMovieTimeBetween(now, targetTime);
//
////        for (MovieReservation reservation : reservations) {
////            // 큐에 메시지 보내기
////            String subject = "영화 예매 알림";
////            String text = "예매하신 영화가 30분 후에 시작됩니다. 영화 시간: " + reservation.getMovieTime();
////            EmailMessage emailMessage = new EmailMessage(reservation.getUserEmail(), subject, text);
////
////            // RabbitMQ 큐에 이메일 메시지를 전송
////            rabbitTemplate.convertAndSend("emailQueue", emailMessage);
////        }
//    }
}
