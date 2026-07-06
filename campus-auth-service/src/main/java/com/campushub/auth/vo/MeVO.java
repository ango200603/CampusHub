package com.campushub.auth.vo;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeVO {
    private Long userId;
    private String phone;
    private List<String> roles;
}
