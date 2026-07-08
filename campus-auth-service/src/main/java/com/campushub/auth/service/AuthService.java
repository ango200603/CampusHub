package com.campushub.auth.service;

import com.campushub.auth.dto.SmsLoginRequest;
import com.campushub.auth.dto.SmsSendRequest;
import com.campushub.auth.vo.LoginVO;
import com.campushub.auth.vo.MeVO;

/**
 * Auth service contract.
 */
public interface AuthService {
    /**
     * Sends an SMS verification code.
     */
    void sendSms(SmsSendRequest request);

    /**
     * Logs in with an SMS verification code.
     */
    LoginVO loginBySms(SmsLoginRequest request);

    /**
     * Logs out the current user.
     */
    void logout(Long userId);

    /**
     * Returns the current user identity.
     */
    MeVO me(String authorization);
}
