package com.campushub.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User service login-user request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserRequest {
    private String phone;
}
