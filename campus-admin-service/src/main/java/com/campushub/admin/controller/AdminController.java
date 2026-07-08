package com.campushub.admin.controller;

import com.campushub.admin.service.AdminService;
import com.campushub.admin.vo.AdminStatsVO;
import com.campushub.admin.vo.DashboardVO;
import com.campushub.common.api.Result;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
    public Result<List<Map<String, Object>>> users() {
        return Result.ok(adminService.users());
    }

    /**
     * Lists recent orders.
     *
     * @return order rows
     */
    @GetMapping("/orders")
    public Result<List<Map<String, Object>>> orders() {
        return Result.ok(adminService.orders());
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
