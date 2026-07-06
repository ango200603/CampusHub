package com.campushub.auth.service;

import com.campushub.auth.dto.SmsLoginRequest;
import com.campushub.auth.dto.SmsSendRequest;
import com.campushub.auth.vo.LoginVO;
import com.campushub.auth.vo.MeVO;

public interface AuthService {
    void sendSms(SmsSendRequest request);

    LoginVO loginBySms(SmsLoginRequest request);

    void logout(Long userId);

    MeVO me(String authorization);
}
