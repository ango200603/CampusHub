package com.campushub.common.security;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Utility facade for JWT operations.
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtService jwtService;

    /**
     * Creates a JWT.
     *
     * @param userId user id
     * @param phone phone number
     * @param roles roles
     * @return signed token
     */
    public String createToken(Long userId, String phone, Collection<String> roles) {
        return jwtService.createToken(userId, phone, roles);
    }

    /**
     * Parses a JWT.
     *
     * @param token token without Bearer prefix
     * @return parsed principal
     */
    public UserPrincipal parseToken(String token) {
        return jwtService.parse(token);
    }
}
