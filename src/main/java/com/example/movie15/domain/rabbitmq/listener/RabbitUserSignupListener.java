package com.example.movie15.domain.rabbitmq.listener;

import com.example.movie15.domain.rabbitmq.common.QueueBindings;
import com.example.movie15.domain.user.entity.Role;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import com.rabbitmq.client.Channel;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitUserSignupListener {

    private final UserRepository userRepository;

    @RabbitListener(queues = QueueBindings.USER_SIGNUP_QUEUE)
    public void deleteUser(Long userId, Channel channel, Message message) throws IOException {
        try {
            User user = userRepository.findByIdOrElseThrow(userId);

            if (!user.isVerified() && user.getTokenExpiryTime().isBefore(LocalDateTime.now()) && user.getRole().equals(Role.USER)) {
                userRepository.delete(user);
                log.info("미인증 유저 삭제 완료: 이메일 : {}", user.getEmail());
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (EntityNotFoundException e) {
            log.warn("유저를 찾을 수 없습니다. userId: {}", userId);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false); // 실패 시 재큐하지 않고 DLQ 로 이동
        }
    }

    @RabbitListener(queues = QueueBindings.GLOBAL_DLQ)
    public void processDeadLetterQueue(Long userId, Channel channel, Message message) throws IOException {
        log.warn("RabbitMQ : 처리 실패한 메시지 발견 : {}", userId);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
