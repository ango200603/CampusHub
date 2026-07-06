package com.campushub.admin.controller;

import com.campushub.admin.service.AdminService;
import com.campushub.admin.vo.AdminStatsVO;
import com.campushub.common.api.Result;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    public Result<List<Map<String, Object>>> users() {
        return Result.ok(adminService.users());
    }

    @GetMapping("/orders")
    public Result<List<Map<String, Object>>> orders() {
        return Result.ok(adminService.orders());
    }

    @GetMapping("/ai/tasks")
    public Result<List<Map<String, Object>>> aiTasks() {
        return Result.ok(adminService.aiTasks());
    }

    @GetMapping("/files")
    public Result<List<Map<String, Object>>> files() {
        return Result.ok(adminService.files());
    }

    @GetMapping("/stats")
    public Result<AdminStatsVO> stats() {
        return Result.ok(adminService.stats());
    }
}
