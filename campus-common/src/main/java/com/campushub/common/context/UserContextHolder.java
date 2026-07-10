package com.campushub.common.context;

/**
 * Thread-local current user holder.
 */
public final class UserContextHolder {
    private static final ThreadLocal<UserContext> HOLDER = new ThreadLocal<>();

    private UserContextHolder() {
    }

    /**
     * Stores current user context.
     *
     * @param context current user context
     */
    public static void set(UserContext context) {
        HOLDER.set(context);
    }

    /**
     * Returns current user context.
     *
     * @return current user context or null
     */
    public static UserContext get() {
        return HOLDER.get();
    }

    /**
     * Returns current user id.
     *
     * @return current user id or null
     */
    public static Long getUserId() {
        UserContext context = HOLDER.get();
        return context == null ? null : context.getUserId();
    }

    /**
     * Clears current thread state.
     */
    public static void clear() {
        HOLDER.remove();
    }
}
