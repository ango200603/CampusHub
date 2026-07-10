package com.campushub.common.context;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Current request user context.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {
    private Long userId;
    private String phone;
    private List<String> roles;

    /**
     * Returns a non-null role list.
     *
     * @return role list
     */
    public List<String> safeRoles() {
        return roles == null ? Collections.emptyList() : roles;
    }
}
