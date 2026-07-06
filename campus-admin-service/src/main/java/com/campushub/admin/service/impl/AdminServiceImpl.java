package com.campushub.admin.service.impl;

import com.campushub.admin.service.AdminService;
import com.campushub.admin.vo.AdminStatsVO;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> users() {
        return jdbcTemplate.queryForList("select id, phone, nickname, points, status, created_at from users order by created_at desc limit 100");
    }

    @Override
    public List<Map<String, Object>> orders() {
        return jdbcTemplate.queryForList("select id, order_no, buyer_id, seller_id, item_id, amount, status, created_at from orders order by created_at desc limit 100");
    }

    @Override
    public List<Map<String, Object>> aiTasks() {
        return jdbcTemplate.queryForList("select id, user_id, file_id, task_type, status, created_at, updated_at from ai_tasks order by created_at desc limit 100");
    }

    @Override
    public List<Map<String, Object>> files() {
        return jdbcTemplate.queryForList("select id, user_id, original_name, file_type, file_size, status, created_at from file_records order by created_at desc limit 100");
    }

    @Override
    public AdminStatsVO stats() {
        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("users", count("users"));
        metrics.put("files", count("file_records"));
        metrics.put("aiTasks", count("ai_tasks"));
        metrics.put("tradeItems", count("trade_items"));
        metrics.put("orders", count("orders"));
        metrics.put("paidOrders", jdbcTemplate.queryForObject("select count(*) from orders where status = 'PAID'", Long.class));
        metrics.put("notices", count("notices"));
        return AdminStatsVO.builder().metrics(metrics).build();
    }

    private Long count(String table) {
        return jdbcTemplate.queryForObject("select count(*) from " + table, Long.class);
    }
}
