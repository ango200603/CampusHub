package com.campushub.notice.controller;

import com.campushub.common.api.Result;
import com.campushub.notice.service.NoticeService;
import com.campushub.notice.vo.NoticeVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping("/my")
    public Result<List<NoticeVO>> my(@RequestHeader("X-User-Id") Long userId) {
        return Result.ok(noticeService.my(userId));
    }

    @PutMapping("/{id}/read")
    public Result<Void> read(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        noticeService.markRead(userId, id);
        return Result.ok();
    }
}
