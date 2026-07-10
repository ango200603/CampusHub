package com.campushub.auth.service.impl;

import com.campushub.auth.client.UserClient;
import com.campushub.auth.dto.LoginUserRequest;
import com.campushub.auth.dto.SmsLoginRequest;
import com.campushub.auth.dto.SmsSendRequest;
import com.campushub.auth.service.AuthService;
import com.campushub.auth.vo.LoginVO;
import com.campushub.auth.vo.MeVO;
import com.campushub.auth.vo.UserVO;
import com.campushub.common.api.Result;
import com.campushub.common.constant.CommonConstant;
import com.campushub.common.constant.RedisKeyConstant;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.exception.ErrorCode;
import com.campushub.common.mq.RabbitKeys;
import com.campushub.common.security.JwtUtil;
import com.campushub.common.security.UserPrincipal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Auth service implementation.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final StringRedisTemplate redisTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final JwtUtil jwtUtil;
    private final UserClient userClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendSms(SmsSendRequest request) {
        String limitKey = RedisKeyConstant.smsLimit(request.getPhone());
        Boolean allowed = redisTemplate.opsForValue().setIfAbsent(limitKey, "1", Duration.ofSeconds(60));
        if (!Boolean.TRUE.equals(allowed)) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, "验证码发送太频繁，请 60 秒后再试");
        }
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        redisTemplate.opsForValue().set(RedisKeyConstant.smsCode(request.getPhone()), code, Duration.ofMinutes(5));
        rabbitTemplate.convertAndSend(
                RabbitKeys.SMS_EXCHANGE,
                RabbitKeys.SMS_SEND_KEY,
                Map.of("phone", request.getPhone(), "code", code, "scene", "login")
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoginVO loginBySms(SmsLoginRequest request) {
        String key = RedisKeyConstant.smsCode(request.getPhone());
        String cached = redisTemplate.opsForValue().get(key);
        if (!request.getCode().equals(cached)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "验证码错误或已过期");
        }
        Result<UserVO> userResult = userClient.getOrCreate(new LoginUserRequest(request.getPhone()));
        if (userResult.getCode() != 0 || userResult.getData() == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "用户服务不可用");
        }
        UserVO user = userResult.getData();
        String token = jwtUtil.createToken(user.getId(), user.getPhone(), List.of(CommonConstant.ROLE_USER));
        redisTemplate.opsForValue().set(RedisKeyConstant.loginToken(user.getId()), token, Duration.ofDays(7));
        redisTemplate.delete(key);
        return LoginVO.builder()
                .token(token)
                .tokenType(CommonConstant.BEARER_TOKEN_TYPE)
                .expiresIn(604800L)
                .user(user)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logout(Long userId) {
        if (userId != null) {
            redisTemplate.delete(RedisKeyConstant.loginToken(userId));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MeVO me(String authorization) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(CommonConstant.BEARER_PREFIX)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        UserPrincipal principal = jwtUtil.parseToken(authorization.substring(CommonConstant.BEARER_PREFIX.length()));
        return MeVO.builder()
                .userId(principal.getUserId() == null ? null : principal.getUserId().toString())
                .phone(principal.getPhone())
                .roles(principal.getRoles())
                .build();
    }
}
