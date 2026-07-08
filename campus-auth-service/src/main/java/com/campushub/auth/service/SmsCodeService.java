package com.campushub.auth.service;

import com.campushub.auth.dto.SendSmsDTO;
import com.campushub.auth.enums.SmsSceneEnum;

/**
 * Service for SMS verification code operations.
 */
public interface SmsCodeService {
    /**
     * Sends a mock SMS verification code.
     *
     * @param request send request
     */
    void send(SendSmsDTO request);

    /**
     * Verifies a SMS code.
     *
     * @param phone phone number
     * @param code verification code
     * @param scene business scene
     * @return true when code is valid
     */
    boolean verify(String phone, String code, SmsSceneEnum scene);
}
