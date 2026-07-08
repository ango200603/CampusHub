package com.campushub.admin.vo;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin dashboard response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardVO {
    private Map<String, Object> metrics;
}
