package com.campushub.notice.controller;

import com.campushub.common.constant.CommonConstant;
import com.campushub.common.api.Result;
import com.campushub.notice.dto.NoticeCreateDTO;
import com.campushub.notice.service.NoticeService;
import com.campushub.notice.vo.NoticeVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Notice endpoints.
 */
@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    /**
     * Creates a notice.
     *
     * @param request create request
     * @return created notice
     */
    @PostMapping
    public Result<NoticeVO> create(@Valid @RequestBody NoticeCreateDTO request) {
        return Result.ok(noticeService.create(request));
    }

    /**
     * Lists current user's notices.
     *
     * @param userId current user id
     * @return notice list
     */
    @GetMapping("/my")
    public Result<List<NoticeVO>> my(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId) {
        return Result.ok(noticeService.my(userId));
    }

    /**
     * Marks a notice as read.
     *
     * @param userId current user id
     * @param id notice id
     * @return empty result
     */
    @PutMapping("/{id}/read")
    public Result<Void> read(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId, @PathVariable Long id) {
        noticeService.markRead(userId, id);
        return Result.ok();
    }
}
