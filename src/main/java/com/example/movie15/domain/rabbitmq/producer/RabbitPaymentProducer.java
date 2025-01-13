package com.example.movie15.domain.rabbitmq.producer;

import com.example.movie15.domain.email.model.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitPaymentProducer {
//
//    private final RabbitTemplate rabbitTemplate;
//
//    // 결제가 완료된 경우 메시지 전송. 결제완료시점에 이 메소드 호출해주면됨.
//    public void sendChargeEvent(String userEmail) {
//        EmailMessage emailMessage = new EmailMessage(
//                userEmail,
//                "영화결제가 완료되었습니다.",
//                "영화결제가 완료되었습니다! 시간에 맞춰 입장해주세요!"
//        );
//
//        // 메시지를 "chargeQueue"로 전송
//        rabbitTemplate.convertAndSend("chargeQueue", emailMessage);
//    }
//
//    // 결제가 취소된 경우 메시지 전송. 결제취소시점에 이 메소드 호출해주면됨.
//    public void sendCancelEvent(String userEmail) {
//        EmailMessage emailMessage = new EmailMessage(userEmail,
//                "영화결제가 취소되었습니다.",
//                "결제취소가 완료되었습니다!"
//        );
//
//        // 메시지를 "cancelQueue"로 전송
//        rabbitTemplate.convertAndSend("cancelQueue", emailMessage);
//    }
}
