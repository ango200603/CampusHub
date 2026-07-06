package com.campushub.admin.vo;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminStatsVO {
    private Map<String, Object> metrics;
}
