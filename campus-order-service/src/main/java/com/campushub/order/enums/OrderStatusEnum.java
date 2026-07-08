package com.campushub.order.enums;

/**
 * Order status.
 */
public enum OrderStatusEnum {
    /**
     * Waiting for payment.
     */
    UNPAID,

    /**
     * Paid order.
     */
    PAID,

    /**
     * Canceled by buyer.
     */
    CANCELED,

    /**
     * Closed by timeout.
     */
    CLOSED
}
