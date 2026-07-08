package com.campushub.common.exception;

import com.campushub.common.api.ResultCode;
import java.io.Serial;
import lombok.Getter;

/**
 * Exception type for expected business failures.
 */
@Getter
public class BusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int code;

    /**
     * Creates an exception using the default message of an error code.
     *
     * @param errorCode error code
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * Creates an exception using a custom message.
     *
     * @param errorCode error code
     * @param message custom message
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    /**
     * Creates an exception using the default message of a result code.
     *
     * @param resultCode result code
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    /**
     * Creates an exception using a result code and custom message.
     *
     * @param resultCode result code
     * @param message custom message
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }
}
