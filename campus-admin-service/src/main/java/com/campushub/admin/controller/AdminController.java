package com.campushub.admin.controller;

import com.campushub.admin.dto.GoodsReviewDTO;
import com.campushub.admin.dto.NoticePublishDTO;
import com.campushub.admin.dto.OrderQueryDTO;
import com.campushub.admin.dto.UserQueryDTO;
import com.campushub.admin.service.AdminService;
import com.campushub.admin.vo.AdminStatsVO;
import com.campushub.admin.vo.DashboardVO;
import com.campushub.common.api.Result;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin endpoints.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    /**
     * Lists recent users.
     *
     * @return user rows
     */
    @GetMapping("/users")
    public Result<List<Map<String, Object>>> users(@ModelAttribute UserQueryDTO query) {
        return Result.ok(adminService.users(query));
    }

    /**
     * Gets user detail from user-service.
     *
     * @param id user id
     * @return user row
     */
    @GetMapping("/users/{id}")
    public Result<Map<String, Object>> userDetail(@PathVariable Long id) {
        return Result.ok(adminService.userDetail(id));
    }

    /**
     * Lists recent orders.
     *
     * @return order rows
     */
    @GetMapping("/orders")
    public Result<List<Map<String, Object>>> orders(@ModelAttribute OrderQueryDTO query) {
        return Result.ok(adminService.orders(query));
    }

    /**
     * Reviews a goods publish request.
     *
     * @param request review request
     * @return review result
     */
    @PostMapping("/goods/review")
    public Result<Map<String, Object>> reviewGoods(@Valid @RequestBody GoodsReviewDTO request) {
        return Result.ok(adminService.reviewGoods(request));
    }

    /**
     * Publishes a notice through notice-service.
     *
     * @param request notice request
     * @return notice result
     */
    @PostMapping("/notices")
    public Result<Map<String, Object>> publishNotice(@Valid @RequestBody NoticePublishDTO request) {
        return Result.ok(adminService.publishNotice(request));
    }

    /**
     * Lists recent AI tasks.
     *
     * @return AI task rows
     */
    @GetMapping("/ai/tasks")
    public Result<List<Map<String, Object>>> aiTasks() {
        return Result.ok(adminService.aiTasks());
    }

    /**
     * Lists recent files.
     *
     * @return file rows
     */
    @GetMapping("/files")
    public Result<List<Map<String, Object>>> files() {
        return Result.ok(adminService.files());
    }

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    @GetMapping("/stats")
    public Result<AdminStatsVO> stats() {
        return Result.ok(adminService.stats());
    }

    /**
     * Returns dashboard metrics.
     *
     * @return dashboard metrics
     */
    @GetMapping("/dashboard")
    public Result<DashboardVO> dashboard() {
        return Result.ok(adminService.dashboard());
    }
}
