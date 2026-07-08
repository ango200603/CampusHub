package com.campushub.common.mq;

import com.campushub.common.constant.MqConstant;

/**
 * Backward compatible RabbitMQ constants used by existing modules.
 */
public final class RabbitKeys {
    public static final String SMS_EXCHANGE = MqConstant.SMS_EXCHANGE;
    public static final String AI_EXCHANGE = MqConstant.AI_EXCHANGE;
    public static final String ORDER_EXCHANGE = MqConstant.ORDER_EXCHANGE;
    public static final String NOTICE_EXCHANGE = MqConstant.NOTICE_EXCHANGE;
    public static final String DEAD_EXCHANGE = MqConstant.DEAD_EXCHANGE;

    public static final String SMS_SEND_QUEUE = MqConstant.SMS_SEND_QUEUE;
    public static final String AI_PARSE_QUEUE = MqConstant.AI_PARSE_QUEUE;
    public static final String ORDER_TIMEOUT_QUEUE = MqConstant.ORDER_TIMEOUT_QUEUE;
    public static final String ORDER_TIMEOUT_DELAY_QUEUE = MqConstant.ORDER_TIMEOUT_DELAY_QUEUE;
    public static final String ORDER_PAY_SUCCESS_QUEUE = MqConstant.ORDER_PAY_SUCCESS_QUEUE;
    public static final String NOTICE_SEND_QUEUE = MqConstant.NOTICE_SEND_QUEUE;
    public static final String DEAD_LETTER_QUEUE = MqConstant.DEAD_LETTER_QUEUE;

    public static final String SMS_SEND_KEY = MqConstant.SMS_SEND_KEY;
    public static final String AI_PARSE_KEY = MqConstant.AI_PARSE_KEY;
    public static final String ORDER_TIMEOUT_KEY = MqConstant.ORDER_TIMEOUT_KEY;
    public static final String ORDER_TIMEOUT_DELAY_KEY = MqConstant.ORDER_TIMEOUT_DELAY_KEY;
    public static final String ORDER_PAY_SUCCESS_KEY = MqConstant.ORDER_PAY_SUCCESS_KEY;
    public static final String NOTICE_SEND_KEY = MqConstant.NOTICE_SEND_KEY;
    public static final String DEAD_KEY = MqConstant.DEAD_KEY;

    private RabbitKeys() {
    }
}
