package com.campushub.common.security;

import com.campushub.common.exception.BusinessException;
import com.campushub.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "campus.jwt")
public class JwtService {
    private String secret = "CampusHubSecretKeyForJwtHS256NeedsAtLeast32Bytes2026";
    private long expireSeconds = 604800;

    public String createToken(Long userId, String phone, Collection<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("phone", phone)
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expireSeconds)))
                .signWith(key())
                .compact();
    }

    @SuppressWarnings("unchecked")
    public UserPrincipal parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Object roles = claims.get("roles");
            List<String> roleList = roles instanceof List<?> list
                    ? list.stream().map(String::valueOf).toList()
                    : List.of("USER");
            return new UserPrincipal(Long.valueOf(claims.getSubject()), claims.get("phone", String.class), roleList);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
