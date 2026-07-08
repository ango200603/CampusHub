package com.campushub.notice.enums;

/**
 * read status enum.
 */
public enum ReadStatus {
    UNREAD(0),
    READ(1);

    private final int code;

    ReadStatus(int code) {
        this.code = code;
    }

    /**
     * Returns the stored code value.
     */
    public int code() {
        return code;
    }
}
