package com.campushub.common.mq;

public final class RabbitKeys {
    public static final String SMS_EXCHANGE = "campus.sms.exchange";
    public static final String AI_EXCHANGE = "campus.ai.exchange";
    public static final String ORDER_EXCHANGE = "campus.order.exchange";
    public static final String NOTICE_EXCHANGE = "campus.notice.exchange";
    public static final String DEAD_EXCHANGE = "campus.dead.exchange";

    public static final String SMS_SEND_QUEUE = "sms.send.queue";
    public static final String AI_PARSE_QUEUE = "ai.parse.queue";
    public static final String ORDER_TIMEOUT_QUEUE = "order.timeout.queue";
    public static final String ORDER_TIMEOUT_DELAY_QUEUE = "order.timeout.delay.queue";
    public static final String ORDER_PAY_SUCCESS_QUEUE = "order.pay.success.queue";
    public static final String NOTICE_SEND_QUEUE = "notice.send.queue";
    public static final String DEAD_LETTER_QUEUE = "dead.letter.queue";

    public static final String SMS_SEND_KEY = "sms.send";
    public static final String AI_PARSE_KEY = "ai.parse";
    public static final String ORDER_TIMEOUT_KEY = "order.timeout";
    public static final String ORDER_TIMEOUT_DELAY_KEY = "order.timeout.delay";
    public static final String ORDER_PAY_SUCCESS_KEY = "order.pay.success";
    public static final String NOTICE_SEND_KEY = "notice.send";
    public static final String DEAD_KEY = "dead";

    private RabbitKeys() {
    }
}
