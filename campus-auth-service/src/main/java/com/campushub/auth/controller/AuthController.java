package com.campushub.auth.controller;

import com.campushub.auth.dto.SmsLoginRequest;
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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sms/send")
    public Result<Void> sendSms(@Valid @RequestBody SmsSendRequest request) {
        authService.sendSms(request);
        return Result.ok();
    }

    @PostMapping("/login/sms")
    public Result<LoginVO> loginBySms(@Valid @RequestBody SmsLoginRequest request) {
        return Result.ok(authService.loginBySms(request));
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        authService.logout(userId);
        return Result.ok();
    }

    @GetMapping("/me")
    public Result<MeVO> me(@RequestHeader("Authorization") String authorization) {
        return Result.ok(authService.me(authorization));
    }
}
