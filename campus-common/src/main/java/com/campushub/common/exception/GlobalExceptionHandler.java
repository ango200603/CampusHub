package com.campushub.common.exception;

import com.campushub.common.api.Result;
import com.campushub.common.api.ResultCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global REST exception handler.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles expected business exceptions.
     *
     * @param ex business exception
     * @return failure result
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException ex) {
        return Result.fail(ex.getCode(), ex.getMessage());
    }

    /**
     * Handles bean validation failures from request bodies.
     *
     * @param ex validation exception
     * @return failure result
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("参数校验失败");
        return Result.fail(ErrorCode.BAD_REQUEST.getCode(), message);
    }

    /**
     * Handles validation failures from method parameters.
     *
     * @param ex constraint violation exception
     * @return failure result
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraint(ConstraintViolationException ex) {
        return Result.fail(ErrorCode.BAD_REQUEST.getCode(), ex.getMessage());
    }

    /**
     * Handles unexpected exceptions.
     *
     * @param ex unexpected exception
     * @return failure result
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        return Result.fail(ResultCode.INTERNAL_ERROR);
    }
}
