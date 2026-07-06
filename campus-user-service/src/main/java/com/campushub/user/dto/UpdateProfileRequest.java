package com.campushub.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @Size(max = 64)
    private String nickname;

    @Size(max = 255)
    private String avatarUrl;
}
