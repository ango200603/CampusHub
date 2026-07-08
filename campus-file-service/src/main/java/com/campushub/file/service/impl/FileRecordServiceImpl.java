package com.campushub.file.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.exception.ErrorCode;
import com.campushub.file.entity.FileRecord;
import com.campushub.file.enums.FileStatus;
import com.campushub.file.enums.FileStatusEnum;
import com.campushub.file.enums.FileTypeEnum;
import com.campushub.file.mapper.FileRecordMapper;
import com.campushub.file.mq.FileParseProducer;
import com.campushub.file.service.FileRecordService;
import com.campushub.file.service.MinioService;
import com.campushub.file.vo.FileRecordVO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * File service implementation.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class FileRecordServiceImpl implements FileRecordService {
    private final FileRecordMapper fileRecordMapper;
    private final MinioService minioService;
    private final FileParseProducer fileParseProducer;

    /**
     * {@inheritDoc}
     */
    @Override
    public FileRecordVO upload(Long userId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "文件不能为空");
        }
        String originalName = StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : "unknown";
        String objectKey = userId + "/" + UUID.randomUUID() + "-" + originalName.replaceAll("[\\s/]+", "_");
        FileTypeEnum coarseType = resolveFileType(file.getContentType(), originalName);
        String fileUrl = minioService.upload(objectKey, file);
        LocalDateTime now = LocalDateTime.now();
        FileRecord record = new FileRecord();
        record.setUserId(userId);
        record.setOriginalName(originalName);
        record.setObjectKey(objectKey);
        record.setFileUrl(fileUrl);
        record.setFileType(StringUtils.hasText(file.getContentType()) ? file.getContentType() : coarseType.name());
        record.setFileSize(file.getSize());
        record.setStatus(FileStatusEnum.UPLOADED.name());
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        fileRecordMapper.insert(record);
        fileParseProducer.send(userId, record.getId(), objectKey, originalName, coarseType.name(), "OCR_SUMMARY");
        return FileRecordVO.from(record);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileRecordVO get(Long userId, Long id) {
        FileRecord record = fileRecordMapper.selectById(id);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "文件不存在");
        }
        return FileRecordVO.from(record);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FileRecordVO> my(Long userId) {
        return fileRecordMapper.selectList(Wrappers.<FileRecord>lambdaQuery()
                        .eq(FileRecord::getUserId, userId)
                        .orderByDesc(FileRecord::getCreatedAt))
                .stream().map(FileRecordVO::from).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long userId, Long id) {
        FileRecord record = fileRecordMapper.selectById(id);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "文件不存在");
        }
        record.setStatus(FileStatus.DELETED.name());
        record.setUpdatedAt(LocalDateTime.now());
        fileRecordMapper.updateById(record);
    }

    private FileTypeEnum resolveFileType(String contentType, String originalName) {
        String value = (contentType == null ? "" : contentType) + " " + originalName;
        String lower = value.toLowerCase();
        if (lower.contains("image/") || lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return FileTypeEnum.IMAGE;
        }
        if (lower.contains("pdf") || lower.endsWith(".pdf")) {
            return FileTypeEnum.PDF;
        }
        return FileTypeEnum.OTHER;
    }
}
