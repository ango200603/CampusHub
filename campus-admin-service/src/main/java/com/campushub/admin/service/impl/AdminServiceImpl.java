package com.campushub.admin.service.impl;

import com.campushub.admin.client.NoticeClient;
import com.campushub.admin.client.UserClient;
import com.campushub.admin.convert.AdminConvert;
import com.campushub.admin.dto.GoodsReviewDTO;
import com.campushub.admin.dto.NoticePublishDTO;
import com.campushub.admin.dto.OrderQueryDTO;
import com.campushub.admin.dto.UserQueryDTO;
import com.campushub.admin.service.AdminService;
import com.campushub.admin.vo.AdminStatsVO;
import com.campushub.admin.vo.DashboardVO;
import com.campushub.common.api.Result;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.exception.ErrorCode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * JDBC based admin query service.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings({"SqlNoDataSourceInspection", "SqlDialectInspection"})
public class AdminServiceImpl implements AdminService {
    private static final int SUCCESS_CODE = 0;
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_NICKNAME = "nickname";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_ORDER_NO = "order_no";
    private static final String COLUMN_BUYER_ID = "buyer_id";
    private static final String COLUMN_SELLER_ID = "seller_id";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_GOODS_ID = "goodsId";
    private static final String KEY_APPROVED = "approved";
    private static final String KEY_STATUS = "status";
    private static final String KEY_REASON = "reason";
    private static final String KEY_MESSAGE = "message";
    private static final String APPROVED_MESSAGE = "商品审核通过";
    private static final String REJECTED_MESSAGE = "商品审核拒绝";
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
    private final UserClient userClient;
    private final NoticeClient noticeClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, Object>> users(UserQueryDTO query) {
        return jdbcTemplate.queryForList(RECENT_USERS_SQL).stream()
                .filter(row -> matchesUser(row, query))
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> userDetail(Long id) {
        return unwrapMap(userClient.get(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, Object>> orders(OrderQueryDTO query) {
        return jdbcTemplate.queryForList(RECENT_ORDERS_SQL).stream()
                .filter(row -> matchesOrder(row, query))
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> reviewGoods(GoodsReviewDTO request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(KEY_GOODS_ID, request.getGoodsId());
        result.put(KEY_APPROVED, request.getApproved());
        result.put(KEY_STATUS, request.getStatus().name());
        result.put(KEY_REASON, request.getReason());
        result.put(KEY_MESSAGE, Boolean.TRUE.equals(request.getApproved()) ? APPROVED_MESSAGE : REJECTED_MESSAGE);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> publishNotice(NoticePublishDTO request) {
        Map<String, Object> notice = new LinkedHashMap<>();
        notice.put(KEY_USER_ID, request.getUserId());
        notice.put(KEY_TITLE, request.getTitle());
        notice.put(KEY_CONTENT, request.getContent());
        return unwrapMap(noticeClient.create(notice));
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
        return AdminConvert.toDashboard(stats());
    }

    private boolean matchesUser(Map<String, Object> row, UserQueryDTO query) {
        if (query == null) {
            return true;
        }
        boolean keywordMatched = !StringUtils.hasText(query.getKeyword())
                || contains(row.get(COLUMN_PHONE), query.getKeyword())
                || contains(row.get(COLUMN_NICKNAME), query.getKeyword());
        boolean statusMatched = query.getStatus() == null || equalsText(row.get(COLUMN_STATUS), query.getStatus());
        return keywordMatched && statusMatched;
    }

    private boolean matchesOrder(Map<String, Object> row, OrderQueryDTO query) {
        if (query == null) {
            return true;
        }
        boolean orderNoMatched = !StringUtils.hasText(query.getOrderNo()) || contains(row.get(COLUMN_ORDER_NO), query.getOrderNo());
        boolean statusMatched = !StringUtils.hasText(query.getStatus()) || equalsText(row.get(COLUMN_STATUS), query.getStatus());
        boolean userMatched = query.getUserId() == null
                || equalsText(row.get(COLUMN_BUYER_ID), query.getUserId())
                || equalsText(row.get(COLUMN_SELLER_ID), query.getUserId());
        return orderNoMatched && statusMatched && userMatched;
    }

    private boolean contains(Object source, String keyword) {
        return source != null && String.valueOf(source).contains(keyword);
    }

    private boolean equalsText(Object source, Object expected) {
        return source != null && String.valueOf(source).equals(String.valueOf(expected));
    }

    private Map<String, Object> unwrapMap(Result<Map<String, Object>> result) {
        if (result == null || result.getCode() != SUCCESS_CODE) {
            String message = result == null ? ErrorCode.CONFLICT.getMessage() : result.getMessage();
            throw new BusinessException(ErrorCode.CONFLICT, message);
        }
        return result.getData() == null ? Map.of() : result.getData();
    }
}
