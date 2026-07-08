package com.campushub.common.constant;

/**
 * Redis key prefixes and builders.
 */
public final class RedisKeyConstant {
    public static final String SMS_CODE_PREFIX = "sms:code:";
    public static final String SMS_LIMIT_PREFIX = "sms:limit:";
    public static final String LOGIN_TOKEN_PREFIX = "login:token:";
    public static final String USER_POINTS_PREFIX = "user:points:";
    public static final String TRADE_ITEM_PREFIX = "trade:item:";
    public static final String TRADE_HOT_LIST = "trade:hot:list";
    public static final String LOCK_TRADE_ITEM_PREFIX = "lock:trade:item:";
    public static final String LOCK_USER_POINTS_PREFIX = "lock:user:points:";
    public static final String LOCK_ORDER_PREFIX = "lock:order:";
    public static final String GATEWAY_LIMIT_PREFIX = "gateway:limit:";

    private RedisKeyConstant() {
    }

    /**
     * Builds a sms code key.
     *
     * @param phone phone number
     * @return Redis key
     */
    public static String smsCode(String phone) {
        return SMS_CODE_PREFIX + phone;
    }

    /**
     * Builds a sms limit key.
     *
     * @param phone phone number
     * @return Redis key
     */
    public static String smsLimit(String phone) {
        return SMS_LIMIT_PREFIX + phone;
    }

    /**
     * Builds a login token key.
     *
     * @param userId user id
     * @return Redis key
     */
    public static String loginToken(Long userId) {
        return LOGIN_TOKEN_PREFIX + userId;
    }

    /**
     * Builds a points cache key.
     *
     * @param userId user id
     * @return Redis key
     */
    public static String userPoints(Long userId) {
        return USER_POINTS_PREFIX + userId;
    }

    /**
     * Builds a trade item cache key.
     *
     * @param itemId item id
     * @return Redis key
     */
    public static String tradeItem(Long itemId) {
        return TRADE_ITEM_PREFIX + itemId;
    }

    /**
     * Builds a trade item lock key.
     *
     * @param itemId item id
     * @return Redis key
     */
    public static String lockTradeItem(Long itemId) {
        return LOCK_TRADE_ITEM_PREFIX + itemId;
    }

    /**
     * Builds a user points lock key.
     *
     * @param userId user id
     * @return Redis key
     */
    public static String lockUserPoints(Long userId) {
        return LOCK_USER_POINTS_PREFIX + userId;
    }

    /**
     * Builds an order lock key.
     *
     * @param orderNo order number
     * @return Redis key
     */
    @SuppressWarnings("unused")
    public static String lockOrder(String orderNo) {
        return LOCK_ORDER_PREFIX + orderNo;
    }

    /**
     * Builds a gateway rate-limit key.
     *
     * @param remote remote address
     * @param path request path
     * @param minute minute bucket
     * @return Redis key
     */
    public static String gatewayLimit(String remote, String path, String minute) {
        return GATEWAY_LIMIT_PREFIX + remote + ":" + path + ":" + minute;
    }
}
