package com.campushub.gateway.config;

import com.campushub.common.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Compatibility class for the older gateway filter name.
 */
@Deprecated
public class JwtAuthenticationFilter extends JwtAuthGlobalFilter {
    /**
     * Creates a compatibility filter instance.
     *
     * @param jwtService JWT service
     * @param gatewayProperties gateway properties
     * @param ignoreAuthProperties ignore auth properties
     * @param redisTemplate Redis template
     * @param objectMapper object mapper
     */
    public JwtAuthenticationFilter(JwtService jwtService,
                                   GatewayProperties gatewayProperties,
                                   IgnoreAuthProperties ignoreAuthProperties,
                                   StringRedisTemplate redisTemplate,
                                   ObjectMapper objectMapper) {
        super(jwtService, gatewayProperties, ignoreAuthProperties, redisTemplate, objectMapper);
    }
}
