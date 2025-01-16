package com.example.movie15.global.config;

import com.example.movie15.domain.rabbitmq.common.QueueBindings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
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

    // 2. 교환 및 큐 생성
    /**
     * Delayed Exchange 설정
     * @return CustomExchange 객체
     */
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

    /**
     * Delayed Email Queue 생성
     * @return Queue 객체
     */
    @Bean
    public Queue emailDelayQueue() {
        log.info("emailDelayQueue 생성 중");
        return new Queue(QueueBindings.EMAIL_DELAY_QUEUE, true);
    }

    /**
     * chargeQueue 생성
     * @return Queue 객체
     */
    @Bean
    public Queue chargeQueue() {
        log.info("chargeQueue 생성 중");
        return new Queue(QueueBindings.CHARGE_QUEUE, true);
    }

    /**
     * cancelQueue 생성
     * @return Queue 객체
     */
    @Bean
    public Queue cancelQueue() {
        log.info("cancelQueue 생성 중");
        return new Queue(QueueBindings.CANCEL_QUEUE, true);
    }

    // 3. 바인딩 설정
    /**
     * Delayed Exchange 와 Email Queue 바인딩
     * @param emailDelayQueue 큐
     * @param delayedExchange 딜레이 익스체인지
     * @return Binding 객체
     */
    @Bean
    public Binding emailDelayQueueBinding(Queue emailDelayQueue, CustomExchange delayedExchange) {
        log.info("emailDelayQueue 를 delayedExchange 에 라우팅 키 ‘emailDelayKey’로 바인딩");
        return BindingBuilder
                .bind(emailDelayQueue)
                .to(delayedExchange)
                .with(QueueBindings.EMAIL_DELAY_KEY)
                .noargs();
    }
}