package com.campushub.file.controller;

import com.campushub.common.constant.CommonConstant;
import com.campushub.common.api.Result;
import com.campushub.file.convert.FileConvert;
import com.campushub.file.service.FileService;
import com.campushub.file.vo.FileRecordVO;
import com.campushub.file.vo.FileUploadVO;
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

/**
 * File API controller.
 */
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class FileController {
    private final FileService fileService;

    /**
     * Uploads a file.
     */
    @PostMapping("/upload")
    public Result<FileUploadVO> upload(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId, @RequestParam("file") MultipartFile file) {
        return Result.ok(FileConvert.toUploadVO(fileService.upload(userId, file)));
    }

    /**
     * Returns a record by id.
     */
    @GetMapping("/{id}")
    public Result<FileRecordVO> get(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId, @PathVariable Long id) {
        return Result.ok(fileService.get(userId, id));
    }

    /**
     * Returns records owned by the current user.
     */
    @GetMapping("/my")
    public Result<List<FileRecordVO>> my(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId) {
        return Result.ok(fileService.my(userId));
    }

    /**
     * Deletes an existing record.
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId, @PathVariable Long id) {
        fileService.delete(userId, id);
        return Result.ok();
    }
}
