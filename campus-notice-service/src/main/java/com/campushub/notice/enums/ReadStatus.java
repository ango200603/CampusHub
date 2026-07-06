package com.campushub.notice.enums;

public enum ReadStatus {
    UNREAD(0),
    READ(1);

    private final int code;

    ReadStatus(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
