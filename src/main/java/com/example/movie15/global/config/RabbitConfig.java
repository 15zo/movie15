package com.example.movie15.global.config;

import com.example.movie15.domain.rabbitmq.common.QueueBindings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitConfig {

    // 1. RabbitMQ 연결 및 템플릿 설정
    /**
     * RabbitMQ와 연결할 ConnectionFactory 설정
     * @return ConnectionFactory 객체
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        log.info("RabbitMQ 연결 구성 중");
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    /**
     * RabbitTemplate 설정
     * @param connectionFactory RabbitMQ 연결 팩토리
     * @return RabbitTemplate 객체
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        log.info("RabbitTemplate 생성 중");
        return new RabbitTemplate(connectionFactory);
    }

    // 2. 교환기생성(Exchange)
    @Bean
    public CustomExchange delayedExchange() {
        log.info("delayedExchange 구성 중");
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
        log.info("userSignupExchange 구성 중");
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
    public CustomExchange deadLetterExchange() {
        log.info("deadLetterExchange 구성 중");
        return new CustomExchange(
                QueueBindings.GLOBAL_DLX,
                "direct",
                true,
                false
        );
    }

    // 3. 큐 생성
    @Bean
    public Queue emailDelayQueue() {
        log.info("emailDelayQueue 생성 중");
        return QueueBuilder.durable(QueueBindings.EMAIL_DELAY_QUEUE)
                .withArgument("x-dead-letter-exchange", QueueBindings.GLOBAL_DLX)
                .withArgument("x-dead-letter-routing-key", QueueBindings.GLOBAL_DLQ_KEY)
                .build();
    }

    @Bean
    public Queue chargeQueue() {
        log.info("chargeQueue 생성 중");
        return QueueBuilder.durable(QueueBindings.CHARGE_QUEUE)
                .withArgument("x-dead-letter-exchange", QueueBindings.GLOBAL_DLX)
                .withArgument("x-dead-letter-routing-key", QueueBindings.GLOBAL_DLQ_KEY)
                .build();
    }

    @Bean
    public Queue cancelQueue() {
        log.info("cancelQueue 생성 중");
        return QueueBuilder.durable(QueueBindings.CANCEL_QUEUE)
                .withArgument("x-dead-letter-exchange", QueueBindings.GLOBAL_DLX)
                .withArgument("x-dead-letter-routing-key", QueueBindings.GLOBAL_DLQ_KEY)
                .build();
    }

    @Bean
    public Queue userSignupQueue() {
        log.info("userSignupQueue 생성 중");
        return QueueBuilder.durable(QueueBindings.USER_SIGNUP_QUEUE)
                .withArgument("x-dead-letter-exchange", QueueBindings.GLOBAL_DLX)
                .withArgument("x-dead-letter-routing-key", QueueBindings.GLOBAL_DLQ_KEY)
                .build();
    }

    @Bean
    public Queue globalDeadLetterQueue() {
        log.info("DLQ 생성 중");
        return new Queue(QueueBindings.GLOBAL_DLQ, true);
    }

    // 4. 바인딩 설정 (교환기와 큐를 연결)
    @Bean
    public Binding emailDelayQueueBinding(Queue emailDelayQueue, CustomExchange delayedExchange) {
        log.info("emailDelayQueue 를 delayedExchange 에 라우팅 키 ‘emailDelayKey’로 바인딩");
        return BindingBuilder
                .bind(emailDelayQueue)
                .to(delayedExchange)
                .with(QueueBindings.EMAIL_DELAY_KEY)
                .noargs();
    }

    @Bean
    public Binding userSignupQueueBinding(Queue userSignupQueue, CustomExchange userSignupExchange) {
        log.info("userSignupQueue 를 userSignupExchange 에 라우팅 키 ‘userSignupKey’로 바인딩");
        return BindingBuilder
                .bind(userSignupQueue)
                .to(userSignupExchange)
                .with(QueueBindings.USER_SIGNUP_KEY)
                .noargs();
    }

    @Bean
    public Binding globalDLQBinding(Queue globalDeadLetterQueue, CustomExchange deadLetterExchange) {
        log.info("globalDeadLetterQueue 를 deadLetterExchange 에 라우팅 키 ‘globalDLQKey’로 바인딩");
        return BindingBuilder
                .bind(globalDeadLetterQueue)
                .to(deadLetterExchange)
                .with(QueueBindings.GLOBAL_DLQ_KEY)
                .noargs();
    }
}