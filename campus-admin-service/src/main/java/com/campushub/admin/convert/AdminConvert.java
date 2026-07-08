package com.campushub.admin.convert;

import com.campushub.admin.vo.AdminStatsVO;
import com.campushub.admin.vo.DashboardVO;

/**
 * Static admin converters.
 */
public final class AdminConvert {
    private AdminConvert() {
    }

    /**
     * Converts stats VO to dashboard VO.
     *
     * @param stats admin stats
     * @return dashboard VO
     */
    public static DashboardVO toDashboard(AdminStatsVO stats) {
        return DashboardVO.builder().metrics(stats.getMetrics()).build();
    }
}
