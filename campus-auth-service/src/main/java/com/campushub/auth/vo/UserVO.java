package com.campushub.auth.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User response used by auth-service clients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    private Long id;
    private String phone;
    private String nickname;
    private String avatarUrl;
    private Integer points;
    private Integer status;
    private LocalDateTime createdAt;
}
