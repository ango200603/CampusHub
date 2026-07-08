package com.campushub.common.api;

import lombok.Getter;

/**
 * Common result codes for API responses.
 */
@Getter
public enum ResultCode {
    /**
     * Request succeeded.
     */
    SUCCESS(0, "ok"),

    /**
     * Request parameters are invalid.
     */
    BAD_REQUEST(400, "请求参数错误"),

    /**
     * Authentication is missing or invalid.
     */
    UNAUTHORIZED(401, "未登录或登录已过期"),

    /**
     * Current user is not allowed to access the resource.
     */
    FORBIDDEN(403, "无权限访问"),

    /**
     * Resource does not exist.
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * Resource state conflicts with the operation.
     */
    CONFLICT(409, "资源状态冲突"),

    /**
     * Request rate is too high.
     */
    TOO_MANY_REQUESTS(429, "请求过于频繁"),

    /**
     * Unhandled server error.
     */
    INTERNAL_ERROR(500, "系统繁忙，请稍后再试");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
