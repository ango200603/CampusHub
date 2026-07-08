package com.campushub.common.constant;

/**
 * General constants shared by modules.
 */
public final class CommonConstant {
    /**
     * Default timezone used by local development.
     */
    @SuppressWarnings("unused")
    public static final String DEFAULT_TIME_ZONE = "Asia/Shanghai";

    /**
     * HTTP authorization Bearer prefix.
     */
    public static final String BEARER_PREFIX = "Bearer ";

    /**
     * HTTP authorization Bearer token type.
     */
    public static final String BEARER_TOKEN_TYPE = "Bearer";

    /**
     * User id header propagated by gateway.
     */
    public static final String HEADER_USER_ID = "X-User-Id";

    /**
     * User phone header propagated by gateway.
     */
    public static final String HEADER_USER_PHONE = "X-User-Phone";

    /**
     * Authorization header name.
     */
    public static final String HEADER_AUTHORIZATION = "Authorization";

    /**
     * Default user role.
     */
    public static final String ROLE_USER = "USER";

    /**
     * Default admin role.
     */
    @SuppressWarnings("unused")
    public static final String ROLE_ADMIN = "ADMIN";

    private CommonConstant() {
    }
}
