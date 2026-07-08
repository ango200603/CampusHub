package com.campushub.admin.service.impl;

import com.campushub.admin.service.AdminService;
import com.campushub.admin.vo.AdminStatsVO;
import com.campushub.admin.vo.DashboardVO;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * JDBC based admin query service.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings({"SqlNoDataSourceInspection", "SqlDialectInspection"})
public class AdminServiceImpl implements AdminService {
    private static final String RECENT_USERS_SQL = """
            select id, phone, nickname, points, status, created_at
            from users
            order by created_at desc
            limit 100
            """;
    private static final String RECENT_ORDERS_SQL = """
            select id, order_no, buyer_id, seller_id, item_id, amount, status, created_at
            from orders
            order by created_at desc
            limit 100
            """;
    private static final String RECENT_AI_TASKS_SQL = """
            select id, user_id, file_id, task_type, status, created_at, updated_at
            from ai_tasks
            order by created_at desc
            limit 100
            """;
    private static final String RECENT_FILES_SQL = """
            select id, user_id, original_name, file_type, file_size, status, created_at
            from file_records
            order by created_at desc
            limit 100
            """;
    private static final String USER_COUNT_SQL = "select count(*) from users";
    private static final String FILE_COUNT_SQL = "select count(*) from file_records";
    private static final String AI_TASK_COUNT_SQL = "select count(*) from ai_tasks";
    private static final String TRADE_ITEM_COUNT_SQL = "select count(*) from trade_items";
    private static final String ORDER_COUNT_SQL = "select count(*) from orders";
    private static final String PAID_ORDER_COUNT_SQL = "select count(*) from orders where status = 'PAID'";
    private static final String NOTICE_COUNT_SQL = "select count(*) from notices";

    private final JdbcTemplate jdbcTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, Object>> users() {
        return jdbcTemplate.queryForList(RECENT_USERS_SQL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, Object>> orders() {
        return jdbcTemplate.queryForList(RECENT_ORDERS_SQL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, Object>> aiTasks() {
        return jdbcTemplate.queryForList(RECENT_AI_TASKS_SQL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, Object>> files() {
        return jdbcTemplate.queryForList(RECENT_FILES_SQL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdminStatsVO stats() {
        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("users", jdbcTemplate.queryForObject(USER_COUNT_SQL, Long.class));
        metrics.put("files", jdbcTemplate.queryForObject(FILE_COUNT_SQL, Long.class));
        metrics.put("aiTasks", jdbcTemplate.queryForObject(AI_TASK_COUNT_SQL, Long.class));
        metrics.put("tradeItems", jdbcTemplate.queryForObject(TRADE_ITEM_COUNT_SQL, Long.class));
        metrics.put("orders", jdbcTemplate.queryForObject(ORDER_COUNT_SQL, Long.class));
        metrics.put("paidOrders", jdbcTemplate.queryForObject(PAID_ORDER_COUNT_SQL, Long.class));
        metrics.put("notices", jdbcTemplate.queryForObject(NOTICE_COUNT_SQL, Long.class));
        return AdminStatsVO.builder().metrics(metrics).build();
    }

    /**
     * Returns dashboard metrics.
     *
     * @return dashboard metrics
     */
    @Override
    public DashboardVO dashboard() {
        return DashboardVO.builder().metrics(stats().getMetrics()).build();
    }
}
