package com.example.movie15.domain.rabbitmq.common;

public final class QueueBindings {

    private QueueBindings() {
        throw new UnsupportedOperationException("인스턴스화 불가능한 클래스");
    }

    // Exchange 이름
    public static final String DELAYED_EXCHANGE = "delayedExchange";

    // Routing Key 이름
    public static final String EMAIL_DELAY_KEY = "emailDelayKey";
    public static final String CHARGE_QUEUE_KEY = "chargeQueue";
    public static final String CANCEL_QUEUE_KEY = "cancelQueue";

    // Queue 이름
    public static final String EMAIL_DELAY_QUEUE = "emailDelayQueue";
    public static final String CHARGE_QUEUE = "chargeQueue";
    public static final String CANCEL_QUEUE = "cancelQueue";
}
