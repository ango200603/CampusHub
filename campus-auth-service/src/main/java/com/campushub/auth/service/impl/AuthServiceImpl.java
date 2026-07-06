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
import com.campushub.common.exception.BusinessException;
import com.campushub.common.exception.ErrorCode;
import com.campushub.common.mq.RabbitKeys;
import com.campushub.common.security.JwtService;
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

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final StringRedisTemplate redisTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final JwtService jwtService;
    private final UserClient userClient;

    @Override
    public void sendSms(SmsSendRequest request) {
        String limitKey = "sms:limit:" + request.getPhone();
        Boolean allowed = redisTemplate.opsForValue().setIfAbsent(limitKey, "1", Duration.ofSeconds(60));
        if (!Boolean.TRUE.equals(allowed)) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, "验证码发送太频繁，请 60 秒后再试");
        }
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        redisTemplate.opsForValue().set("sms:code:" + request.getPhone(), code, Duration.ofMinutes(5));
        rabbitTemplate.convertAndSend(
                RabbitKeys.SMS_EXCHANGE,
                RabbitKeys.SMS_SEND_KEY,
                Map.of("phone", request.getPhone(), "code", code, "scene", "login")
        );
    }

    @Override
    public LoginVO loginBySms(SmsLoginRequest request) {
        String key = "sms:code:" + request.getPhone();
        String cached = redisTemplate.opsForValue().get(key);
        if (!request.getCode().equals(cached)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "验证码错误或已过期");
        }
        Result<UserVO> userResult = userClient.getOrCreate(new LoginUserRequest(request.getPhone()));
        if (userResult.getCode() != 0 || userResult.getData() == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "用户服务不可用");
        }
        UserVO user = userResult.getData();
        String token = jwtService.createToken(user.getId(), user.getPhone(), List.of("USER"));
        redisTemplate.opsForValue().set("login:token:" + user.getId(), token, Duration.ofDays(7));
        redisTemplate.delete(key);
        return LoginVO.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(604800L)
                .user(user)
                .build();
    }

    @Override
    public void logout(Long userId) {
        if (userId != null) {
            redisTemplate.delete("login:token:" + userId);
        }
    }

    @Override
    public MeVO me(String authorization) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        UserPrincipal principal = jwtService.parse(authorization.substring(7));
        return MeVO.builder()
                .userId(principal.getUserId())
                .phone(principal.getPhone())
                .roles(principal.getRoles())
                .build();
    }
}
