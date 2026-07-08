package com.campushub.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Business number generator.
 */
public final class NoGenerator {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    /**
     * Generates an order number.
     *
     * @return order number
     */
    public static String orderNo() {
        return "CH" + FORMATTER.format(LocalDateTime.now()) + random();
    }

    /**
     * Generates a payment number.
     *
     * @return payment number
     */
    public static String payNo() {
        return "PAY" + FORMATTER.format(LocalDateTime.now()) + random();
    }

    private static int random() {
        return ThreadLocalRandom.current().nextInt(1000, 10000);
    }

    private NoGenerator() {
    }
}
