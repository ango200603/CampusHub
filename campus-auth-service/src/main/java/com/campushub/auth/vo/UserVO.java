package com.campushub.auth.vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String phone;
    private String nickname;
    private String avatarUrl;
    private Integer points;
    private Integer status;
    private LocalDateTime createdAt;
}
