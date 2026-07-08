package com.campushub.auth.service.impl;

import com.campushub.auth.dto.SendSmsDTO;
import com.campushub.auth.dto.SmsSendRequest;
import com.campushub.auth.enums.SmsSceneEnum;
import com.campushub.auth.service.SmsCodeService;
import com.campushub.common.constant.MqConstant;
import com.campushub.common.constant.RedisKeyConstant;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.exception.ErrorCode;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Redis and RabbitMQ based SMS code service.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class SmsCodeServiceImpl implements SmsCodeService {
    private final StringRedisTemplate redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    /**
     * Sends a mock SMS verification code.
     *
     * @param request send request
     */
    @Override
    public void send(SendSmsDTO request) {
        SmsSendRequest sendRequest = request.toSmsSendRequest();
        String phone = sendRequest.getPhone();
        String limitKey = RedisKeyConstant.smsLimit(phone);
        Boolean allowed = redisTemplate.opsForValue().setIfAbsent(limitKey, "1", Duration.ofSeconds(60));
        if (!Boolean.TRUE.equals(allowed)) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, "验证码发送太频繁，请 60 秒后再试");
        }
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        redisTemplate.opsForValue().set(RedisKeyConstant.smsCode(phone), code, Duration.ofMinutes(5));
        rabbitTemplate.convertAndSend(MqConstant.SMS_EXCHANGE, MqConstant.SMS_SEND_KEY,
                Map.of("phone", phone, "code", code, "scene", request.getScene() == null ? SmsSceneEnum.LOGIN.name() : request.getScene()));
    }

    /**
     * Verifies a SMS code.
     *
     * @param phone phone number
     * @param code verification code
     * @param scene business scene
     * @return true when code is valid
     */
    @Override
    public boolean verify(String phone, String code, SmsSceneEnum scene) {
        String cached = redisTemplate.opsForValue().get(RedisKeyConstant.smsCode(phone));
        return code != null && code.equals(cached);
    }
}
