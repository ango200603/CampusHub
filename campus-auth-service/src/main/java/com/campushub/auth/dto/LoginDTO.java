package com.campushub.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for SMS login.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotBlank
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank
    @Pattern(regexp = "^\\d{6}$", message = "验证码必须是 6 位数字")
    private String code;

    /**
     * Converts this DTO to the existing login request type.
     *
     * @return SMS login request
     */
    public SmsLoginRequest toSmsLoginRequest() {
        SmsLoginRequest request = new SmsLoginRequest();
        request.setPhone(phone);
        request.setCode(code);
        return request;
    }
}
