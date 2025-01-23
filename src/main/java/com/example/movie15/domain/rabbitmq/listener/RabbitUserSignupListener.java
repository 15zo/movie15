package com.example.movie15.domain.rabbitmq.listener;

import com.example.movie15.domain.rabbitmq.common.QueueBindings;
import com.example.movie15.domain.user.entity.Role;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitUserSignupListener {

    private final UserRepository userRepository;

    @RabbitListener(queues = QueueBindings.USER_SIGNUP_QUEUE)
    public void deleteUser(Long userId) {
        try {
            User user = userRepository.findByIdOrElseThrow(userId);

            if (!user.isVerified() && user.getTokenExpiryTime().isBefore(LocalDateTime.now()) && user.getRole().equals(Role.USER)) {
                userRepository.delete(user);
                log.info("미인증 유저 삭제 완료: 이메일 : {}", user.getEmail());
            }
        } catch (EntityNotFoundException e) {
            log.warn("유저를 찾을 수 없습니다. userId: {}", userId);
            throw new IllegalArgumentException("RabbitMQ deleteUser 오류");
        }
    }
}
