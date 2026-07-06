package com.campushub.file.controller;

import com.campushub.common.api.Result;
import com.campushub.file.service.FileRecordService;
import com.campushub.file.vo.FileRecordVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {
    private final FileRecordService fileRecordService;

    @PostMapping("/upload")
    public Result<FileRecordVO> upload(@RequestHeader("X-User-Id") Long userId, @RequestParam("file") MultipartFile file) {
        return Result.ok(fileRecordService.upload(userId, file));
    }

    @GetMapping("/{id}")
    public Result<FileRecordVO> get(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        return Result.ok(fileRecordService.get(userId, id));
    }

    @GetMapping("/my")
    public Result<List<FileRecordVO>> my(@RequestHeader("X-User-Id") Long userId) {
        return Result.ok(fileRecordService.my(userId));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        fileRecordService.delete(userId, id);
        return Result.ok();
    }
}
