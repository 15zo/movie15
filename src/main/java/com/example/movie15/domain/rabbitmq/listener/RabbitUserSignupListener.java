package com.example.movie15.domain.rabbitmq.listener;

import com.example.movie15.domain.rabbitmq.common.QueueBindings;
import com.example.movie15.domain.user.entity.Role;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import com.rabbitmq.client.Channel;
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

    @RabbitListener(queues = QueueBindings.USER_SIGNUP_QUEUE, ackMode = "MANUAL")
    public void deleteUser(Long userId, Channel channel, Message message) throws Exception {
        try {
            User user = userRepository.findByIdOrElseThrow(userId);

            if (!user.isVerified() && user.getTokenExpiryTime().isBefore(LocalDateTime.now()) && user.getRole().equals(Role.USER)) {
                userRepository.delete(user);
                log.info("미인증 유저 삭제 완료: 이메일 : {}", user.getEmail());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        } catch (Exception e) {
            log.warn("미인증 유저를 찾을 수 없습니다. DLQ 로 발송. USER 아이디 : {}", userId);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);  // 재큐하지 않고 DLQ 로 이동
        }
    }

}
