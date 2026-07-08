package com.campushub.notice.enums;

/**
 * Notice read status.
 */
public enum NoticeStatusEnum {
    /**
     * Unread notice.
     */
    UNREAD(0),

    /**
     * Read notice.
     */
    READ(1);

    private final int code;

    NoticeStatusEnum(int code) {
        this.code = code;
    }

    /**
     * Returns persisted code.
     *
     * @return persisted code
     */
    public int code() {
        return code;
    }
}
