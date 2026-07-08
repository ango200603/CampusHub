package com.campushub.ai.controller;

import com.campushub.common.constant.CommonConstant;
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

/**
 * Ai API controller.
 */
@RestController
@RequestMapping("/ai/tasks")
@RequiredArgsConstructor
public class AiTaskController {
    private final AiTaskService aiTaskService;

    /**
     * Creates a new record.
     */
    @PostMapping
    public Result<AiTaskVO> create(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId,
                                   @Valid @RequestBody CreateAiTaskRequest request) {
        return Result.ok(aiTaskService.create(userId, request));
    }

    /**
     * Returns a record by id.
     */
    @GetMapping("/{id}")
    public Result<AiTaskVO> get(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId, @PathVariable Long id) {
        return Result.ok(aiTaskService.get(userId, id));
    }

    /**
     * Returns records owned by the current user.
     */
    @GetMapping("/my")
    public Result<List<AiTaskVO>> my(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId) {
        return Result.ok(aiTaskService.my(userId));
    }
}
