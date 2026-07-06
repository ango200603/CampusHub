package com.campushub.ai.controller;

import com.campushub.ai.dto.CreateAiTaskRequest;
import com.campushub.ai.service.AiTaskService;
import com.campushub.ai.vo.AiTaskVO;
import com.campushub.common.api.Result;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/tasks")
@RequiredArgsConstructor
public class AiTaskController {
    private final AiTaskService aiTaskService;

    @PostMapping
    public Result<AiTaskVO> create(@RequestHeader("X-User-Id") Long userId,
                                   @Valid @RequestBody CreateAiTaskRequest request) {
        return Result.ok(aiTaskService.create(userId, request));
    }

    @GetMapping("/{id}")
    public Result<AiTaskVO> get(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        return Result.ok(aiTaskService.get(userId, id));
    }

    @GetMapping("/my")
    public Result<List<AiTaskVO>> my(@RequestHeader("X-User-Id") Long userId) {
        return Result.ok(aiTaskService.my(userId));
    }
}
