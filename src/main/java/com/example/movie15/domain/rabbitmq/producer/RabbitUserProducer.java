package com.example.movie15.domain.rabbitmq.producer;

import com.example.movie15.domain.rabbitmq.common.QueueBindings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitUserProducer {

    private final RabbitTemplate rabbitTemplate;

    public void userSignupEvent(Long userId) {
        rabbitTemplate.convertAndSend(
                QueueBindings.USER_SIGNUP_EXCHANGE,
                QueueBindings.USER_SIGNUP_KEY,
                userId,
                message -> {
                    message.getMessageProperties().setHeader("x-delay", 600_000L);
                    message.getMessageProperties().setExpiration(String.valueOf(1_200_000L));
                    return message;
                }
        );
    }
}
