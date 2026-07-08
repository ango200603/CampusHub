package com.campushub.gateway.config;

import com.campushub.common.api.Result;
import com.campushub.common.constant.CommonConstant;
import com.campushub.common.constant.RedisKeyConstant;
import com.campushub.common.security.JwtService;
import com.campushub.common.security.UserPrincipal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Gateway global filter for rate limiting and JWT authentication.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {
    private static final DateTimeFormatter MINUTE = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    private final JwtService jwtService;
    private final GatewayProperties gatewayProperties;
    private final IgnoreAuthProperties ignoreAuthProperties;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * Applies gateway rate limiting, whitelist checks and JWT parsing.
     *
     * @param exchange current exchange
     * @param chain gateway chain
     * @return completion signal
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            return chain.filter(exchange);
        }
        if (!allowByRateLimit(exchange, path)) {
            return write(exchange, HttpStatus.TOO_MANY_REQUESTS, Result.fail(429, "网关限流，请稍后再试"));
        }
        if (isWhite(path)) {
            return chain.filter(exchange);
        }
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith(CommonConstant.BEARER_PREFIX)) {
            return write(exchange, HttpStatus.UNAUTHORIZED, Result.fail(401, "请先登录"));
        }
        UserPrincipal principal = jwtService.parse(authorization.substring(CommonConstant.BEARER_PREFIX.length()));
        ServerHttpRequest mutated = request.mutate()
                .header(CommonConstant.HEADER_USER_ID, String.valueOf(principal.getUserId()))
                .header(CommonConstant.HEADER_USER_PHONE, principal.getPhone() == null ? "" : principal.getPhone())
                .build();
        return chain.filter(exchange.mutate().request(mutated).build());
    }

    /**
     * Returns filter order.
     *
     * @return order value
     */
    @Override
    public int getOrder() {
        return -100;
    }

    private boolean isWhite(String path) {
        return gatewayProperties.getWhitelist().stream().anyMatch(pattern -> pathMatcher.match(pattern, path))
                || ignoreAuthProperties.getPaths().stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private boolean allowByRateLimit(ServerWebExchange exchange, String path) {
        try {
            String remote = exchange.getRequest().getRemoteAddress() == null
                    ? "unknown"
                    : exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            String key = RedisKeyConstant.gatewayLimit(remote, path, MINUTE.format(LocalDateTime.now()));
            Long count = redisTemplate.opsForValue().increment(key);
            if (count != null && count == 1L) {
                redisTemplate.expire(key, Duration.ofMinutes(1));
            }
            return count == null || count <= gatewayProperties.getRateLimitPerMinute();
        } catch (Exception ex) {
            log.warn("Gateway rate limit skipped: {}", ex.getMessage());
            return true;
        }
    }

    private Mono<Void> write(ServerWebExchange exchange, HttpStatus status, Result<?> result) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body;
        try {
            body = objectMapper.writeValueAsString(result).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException ex) {
            body = "{\"code\":500,\"message\":\"网关异常\"}".getBytes(StandardCharsets.UTF_8);
        }
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
