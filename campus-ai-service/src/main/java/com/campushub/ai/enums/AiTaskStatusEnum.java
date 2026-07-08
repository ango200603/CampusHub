package com.campushub.ai.enums;

/**
 * AI task status.
 */
public enum AiTaskStatusEnum {
    /**
     * Waiting for consumption.
     */
    PENDING,

    /**
     * Currently processing.
     */
    PROCESSING,

    /**
     * Successfully parsed.
     */
    SUCCESS,

    /**
     * Parsing failed.
     */
    FAILED
}
