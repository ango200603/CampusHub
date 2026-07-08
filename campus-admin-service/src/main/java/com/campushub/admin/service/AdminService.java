package com.campushub.admin.service;

import com.campushub.admin.vo.AdminStatsVO;
import com.campushub.admin.vo.DashboardVO;
import java.util.List;
import java.util.Map;

/**
 * Admin query service.
 */
public interface AdminService {
    /**
     * Lists recent users.
     *
     * @return user rows
     */
    List<Map<String, Object>> users();

    /**
     * Lists recent orders.
     *
     * @return order rows
     */
    List<Map<String, Object>> orders();

    /**
     * Lists recent AI tasks.
     *
     * @return AI task rows
     */
    List<Map<String, Object>> aiTasks();

    /**
     * Lists recent file records.
     *
     * @return file rows
     */
    List<Map<String, Object>> files();

    /**
     * Returns admin statistics.
     *
     * @return statistics
     */
    AdminStatsVO stats();

    /**
     * Returns dashboard metrics.
     *
     * @return dashboard metrics
     */
    DashboardVO dashboard();
}
