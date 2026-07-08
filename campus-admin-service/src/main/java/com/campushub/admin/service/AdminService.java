package com.campushub.admin.service;

import com.campushub.admin.dto.GoodsReviewDTO;
import com.campushub.admin.dto.NoticePublishDTO;
import com.campushub.admin.dto.OrderQueryDTO;
import com.campushub.admin.dto.UserQueryDTO;
import com.campushub.admin.vo.AdminStatsVO;
import com.campushub.admin.vo.DashboardVO;
import java.util.List;
import java.util.Map;

/**
 * Admin query service.
 */
public interface AdminService {
    /**
     * Lists users by query parameters.
     *
     * @param query query parameters
     * @return user rows
     */
    List<Map<String, Object>> users(UserQueryDTO query);

    /**
     * Gets user detail from user-service.
     *
     * @param id user id
     * @return user row
     */
    Map<String, Object> userDetail(Long id);

    /**
     * Lists orders by query parameters.
     *
     * @param query query parameters
     * @return order rows
     */
    List<Map<String, Object>> orders(OrderQueryDTO query);

    /**
     * Reviews a goods publish request.
     *
     * @param request review request
     * @return review result
     */
    Map<String, Object> reviewGoods(GoodsReviewDTO request);

    /**
     * Publishes a notice through notice-service.
     *
     * @param request notice request
     * @return notice result
     */
    Map<String, Object> publishNotice(NoticePublishDTO request);

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
