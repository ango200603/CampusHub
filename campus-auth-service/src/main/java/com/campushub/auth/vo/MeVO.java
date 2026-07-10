package com.campushub.auth.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Current user response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeVO {
    private String userId;
    private String phone;
    private List<String> roles;
}
