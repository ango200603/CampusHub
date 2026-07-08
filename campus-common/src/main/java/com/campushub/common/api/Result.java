package com.campushub.common.api;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard API response wrapper.
 *
 * @param <T> response data type
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    /**
     * Builds a success response with data.
     *
     * @param data response data
     * @param <T> response data type
     * @return success result
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(0, "ok", data, LocalDateTime.now());
    }

    /**
     * Builds a success response without data.
     *
     * @return success result
     */
    public static Result<Void> ok() {
        return new Result<>(0, "ok", null, LocalDateTime.now());
    }

    /**
     * Builds a failure response.
     *
     * @param code business code
     * @param message business message
     * @param <T> response data type
     * @return failure result
     */
    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null, LocalDateTime.now());
    }

    /**
     * Builds a failure response from a predefined result code.
     *
     * @param resultCode result code
     * @param <T> response data type
     * @return failure result
     */
    public static <T> Result<T> fail(ResultCode resultCode) {
        return fail(resultCode.getCode(), resultCode.getMessage());
    }
}
