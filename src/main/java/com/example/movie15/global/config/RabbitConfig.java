package com.example.movie15.global.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
//
//    // 이메일 발송을 위한 큐 설정
//    @Bean
//    public Queue emailQueue() {
//        return new Queue("emailQueue", true);  // true: 큐가 durable, 서버 재시작 후에도 큐가 유지됨
//    }
//
//    // 영화결제이메일 발송을 위한 큐 설정
//    @Bean
//    public Queue chargeQueue() {
//        return new Queue("chargeQueue", true);  // true: 큐가 durable, 서버 재시작 후에도 큐가 유지됨
//    }
//
//    // 영화결제취소이메일 발송을 위한 큐 설정
//    @Bean
//    public Queue cancelQueue() {
//        return new Queue("cancelQueue", true);  // true: 큐가 durable, 서버 재시작 후에도 큐가 유지됨
//    }
//
//    // RabbitMQ와 연결할 ConnectionFactory 설정
//    @Bean
//    public ConnectionFactory connectionFactory() {
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost"); // RabbitMQ 호스트
//        connectionFactory.setUsername("guest");
//        connectionFactory.setPassword("guest");
//        return connectionFactory;
//    }
//
//    // RabbitTemplate 설정
//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        return new RabbitTemplate(connectionFactory);
//    }
}
