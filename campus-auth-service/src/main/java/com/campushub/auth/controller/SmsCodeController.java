package com.campushub.auth.controller;

import com.campushub.auth.dto.SendSmsDTO;
import com.campushub.auth.enums.SmsSceneEnum;
import com.campushub.auth.service.SmsCodeService;
import com.campushub.common.api.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * SMS code endpoints.
 */
@RestController
@RequestMapping("/auth/sms-code")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class SmsCodeController {
    private final SmsCodeService smsCodeService;

    /**
     * Sends a mock SMS code.
     *
     * @param request send request
     * @return empty result
     */
    @PostMapping("/send")
    public Result<Void> send(@Valid @RequestBody SendSmsDTO request) {
        smsCodeService.send(request);
        return Result.ok();
    }

    /**
     * Verifies a mock SMS code.
     *
     * @param phone phone number
     * @param code verification code
     * @param scene business scene
     * @return verification result
     */
    @PostMapping("/verify")
    public Result<Boolean> verify(@RequestParam String phone,
                                  @RequestParam String code,
                                  @RequestParam(required = false) SmsSceneEnum scene) {
        SmsSceneEnum safeScene = scene == null ? SmsSceneEnum.LOGIN : scene;
        return Result.ok(smsCodeService.verify(phone, code, safeScene));
    }
}
