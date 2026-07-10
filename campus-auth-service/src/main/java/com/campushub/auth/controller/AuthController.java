package com.campushub.auth.controller;

import com.campushub.common.constant.CommonConstant;
import com.campushub.auth.dto.LoginDTO;
import com.campushub.auth.dto.SmsSendRequest;
import com.campushub.auth.service.AuthService;
import com.campushub.auth.vo.LoginVO;
import com.campushub.auth.vo.MeVO;
import com.campushub.common.api.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Auth API controller.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * Sends an SMS verification code.
     */
    @PostMapping("/sms/send")
    public Result<Void> sendSms(@Valid @RequestBody SmsSendRequest request) {
        authService.sendSms(request);
        return Result.ok();
    }

    /**
     * Logs in with an SMS verification code.
     */
    @PostMapping("/login/sms")
    public Result<LoginVO> loginBySms(@Valid @RequestBody LoginDTO request) {
        return Result.ok(authService.loginBySms(request.toSmsLoginRequest()));
    }

    /**
     * Logs out the current user.
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = CommonConstant.HEADER_USER_ID, required = false) Long userId) {
        authService.logout(userId);
        return Result.ok();
    }

    /**
     * Returns the current user identity.
     */
    @GetMapping("/me")
    public Result<MeVO> me(@RequestHeader(CommonConstant.HEADER_AUTHORIZATION) String authorization) {
        return Result.ok(authService.me(authorization));
    }
}
