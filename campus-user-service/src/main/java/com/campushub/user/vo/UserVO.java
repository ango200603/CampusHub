package com.campushub.user.vo;

import com.campushub.user.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserVO {
    private Long id;
    private String phone;
    private String nickname;
    private String avatarUrl;
    private Integer points;
    private Integer status;
    private LocalDateTime createdAt;

    public static UserVO from(User user) {
        if (user == null) {
            return null;
        }
        return UserVO.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .points(user.getPoints())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
