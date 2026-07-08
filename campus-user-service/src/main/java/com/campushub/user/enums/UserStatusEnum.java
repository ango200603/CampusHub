package com.campushub.user.enums;

/**
 * User account status.
 */
public enum UserStatusEnum {
    /**
     * Normal user.
     */
    ENABLED(1),

    /**
     * Disabled user.
     */
    DISABLED(0);

    private final int code;

    UserStatusEnum(int code) {
        this.code = code;
    }

    /**
     * Returns persisted status code.
     *
     * @return status code
     */
    public int code() {
        return code;
    }
}
