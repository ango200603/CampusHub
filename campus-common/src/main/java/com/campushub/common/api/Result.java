package com.campushub.common.api;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> Result<T> ok(T data) {
        return new Result<>(0, "ok", data, LocalDateTime.now());
    }

    public static Result<Void> ok() {
        return new Result<>(0, "ok", null, LocalDateTime.now());
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null, LocalDateTime.now());
    }
}
