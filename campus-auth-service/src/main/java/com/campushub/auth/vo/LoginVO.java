package com.campushub.auth.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginVO {
    private String token;
    private String tokenType;
    private Long expiresIn;
    private UserVO user;
}
