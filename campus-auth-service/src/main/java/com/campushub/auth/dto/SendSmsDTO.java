package com.campushub.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for sending a SMS verification code.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendSmsDTO {
    @NotBlank
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    private String scene;

    /**
     * Converts this DTO to the existing send request type.
     *
     * @return SMS send request
     */
    public SmsSendRequest toSmsSendRequest() {
        SmsSendRequest request = new SmsSendRequest();
        request.setPhone(phone);
        return request;
    }
}
