package com.example.movie15.domain.rabbitmq.common;

public final class RedisKey {

    private RedisKey() {
        throw new UnsupportedOperationException("인스턴스화 불가능한 클래스");
    }

    public static final String REMINDER_KEY = "reminderEmailQueue";
}
