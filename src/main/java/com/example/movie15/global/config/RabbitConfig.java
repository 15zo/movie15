package com.example.movie15.global.config;

import com.example.movie15.domain.rabbitmq.common.QueueBindings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitConfig {

    @Value("${spring.rabbitmq.host:localhost}")
    private String host;

    @Value("${spring.rabbitmq.username:guest}")
    private String username;

    @Value("${spring.rabbitmq.password:guest}")
    private String password;
    // 1. RabbitMQ 연결 및 템플릿 설정
    /**
     * RabbitMQ와 연결할 ConnectionFactory 설정
     * @return ConnectionFactory 객체
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    /**
     * RabbitTemplate 설정
     * @param connectionFactory RabbitMQ 연결 팩토리
     * @return RabbitTemplate 객체
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    // 2. 교환기생성(Exchange)
    @Bean
    public CustomExchange delayedExchange() {
        return new CustomExchange(
                QueueBindings.DELAYED_EXCHANGE,
                "x-delayed-message", // 지연 메시지를 위한 타입
                true, // durable (서버 재시작 시 유지)
                false // auto-delete (자동 삭제하지 않음)
        ) {
            {
                getArguments().put("x-delayed-type", "direct");
            }
        };
    }

    @Bean
    public CustomExchange userSignupExchange() {
        return new CustomExchange(
                QueueBindings.USER_SIGNUP_EXCHANGE,
                "x-delayed-message",
                true,
                false
        ) {
            {
                getArguments().put("x-delayed-type", "direct");
            }
        };
    }

    @Bean
    public CustomExchange userDeadLetterExchange() {
        return new CustomExchange(
                QueueBindings.USER_DLX,
                "direct",
                true,
                false
        );
    }

    @Bean
    public CustomExchange paymentDeadLetterExchange() {
        return new CustomExchange(
                QueueBindings.PAYMENT_DLX,
                "direct",
                true,
                false
        );
    }

    // 3. 큐 생성
    @Bean
    public Queue emailDelayQueue() {
        return QueueBuilder.durable(QueueBindings.EMAIL_DELAY_QUEUE)
                .withArgument("x-dead-letter-exchange", QueueBindings.PAYMENT_DLX)
                .withArgument("x-dead-letter-routing-key", QueueBindings.PAYMENT_DLQ_KEY)
                .build();
    }

    @Bean
    public Queue chargeQueue() {
        return QueueBuilder.durable(QueueBindings.CHARGE_QUEUE)
                .withArgument("x-dead-letter-exchange", QueueBindings.PAYMENT_DLX)
                .withArgument("x-dead-letter-routing-key", QueueBindings.PAYMENT_DLQ_KEY)
                .build();
    }

    @Bean
    public Queue cancelQueue() {
        return QueueBuilder.durable(QueueBindings.CANCEL_QUEUE)
                .withArgument("x-dead-letter-exchange", QueueBindings.PAYMENT_DLX)
                .withArgument("x-dead-letter-routing-key", QueueBindings.PAYMENT_DLQ_KEY)
                .build();
    }

    @Bean
    public Queue userSignupQueue() {
        return QueueBuilder.durable(QueueBindings.USER_SIGNUP_QUEUE)
                .withArgument("x-dead-letter-exchange", QueueBindings.USER_DLX)
                .withArgument("x-dead-letter-routing-key", QueueBindings.USER_DLQ_KEY)
                .build();
    }

    // 4. DLQ 생성 - 실패한 메시지들 오는 큐
    @Bean
    public Queue userDeadLetterQueue() {
        return new Queue(QueueBindings.USER_DLQ, true);
    }

    @Bean
    public Queue paymentDeadLetterQueue() {
        return new Queue(QueueBindings.PAYMENT_DLQ, true);
    }

    // 4. 바인딩 설정 (교환기와 큐를 연결)
    @Bean
    public Binding emailDelayQueueBinding(Queue emailDelayQueue, CustomExchange delayedExchange) {
        return BindingBuilder
                .bind(emailDelayQueue)
                .to(delayedExchange)
                .with(QueueBindings.EMAIL_DELAY_KEY)
                .noargs();
    }

    @Bean
    public Binding userSignupQueueBinding(Queue userSignupQueue, CustomExchange userSignupExchange) {
        return BindingBuilder
                .bind(userSignupQueue)
                .to(userSignupExchange)
                .with(QueueBindings.USER_SIGNUP_KEY)
                .noargs();
    }

    @Bean
    public Binding userDLQBinding(Queue userDeadLetterQueue, CustomExchange userDeadLetterExchange) {
        return BindingBuilder
                .bind(userDeadLetterQueue)
                .to(userDeadLetterExchange)
                .with(QueueBindings.USER_DLQ_KEY)
                .noargs();
    }

    @Bean
    public Binding paymentDLQBinding(Queue paymentDeadLetterQueue, CustomExchange paymentDeadLetterExchange) {
        return BindingBuilder
                .bind(paymentDeadLetterQueue)
                .to(paymentDeadLetterExchange)
                .with(QueueBindings.PAYMENT_DLQ_KEY)
                .noargs();
    }
}