package com.campushub.file.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.exception.ErrorCode;
import com.campushub.common.mq.RabbitKeys;
import com.campushub.file.config.MinioProperties;
import com.campushub.file.entity.FileRecord;
import com.campushub.file.enums.FileStatus;
import com.campushub.file.mapper.FileRecordMapper;
import com.campushub.file.service.FileRecordService;
import com.campushub.file.vo.FileRecordVO;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * File service implementation.
 */
@Service
@RequiredArgsConstructor
public class FileRecordServiceImpl implements FileRecordService {
    private final FileRecordMapper fileRecordMapper;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final RabbitTemplate rabbitTemplate;

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
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(objectKey)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "文件上传失败：" + ex.getMessage());
        }
        LocalDateTime now = LocalDateTime.now();
        FileRecord record = new FileRecord();
        record.setUserId(userId);
        record.setOriginalName(originalName);
        record.setObjectKey(objectKey);
        record.setFileUrl(minioProperties.getEndpoint() + "/" + minioProperties.getBucket() + "/" + objectKey);
        record.setFileType(file.getContentType());
        record.setFileSize(file.getSize());
        record.setStatus(FileStatus.UPLOADED.name());
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        fileRecordMapper.insert(record);
        rabbitTemplate.convertAndSend(RabbitKeys.AI_EXCHANGE, RabbitKeys.AI_PARSE_KEY, Map.of(
                "userId", userId,
                "fileId", record.getId(),
                "objectKey", objectKey,
                "originalName", originalName,
                "taskType", "OCR_SUMMARY"
        ));
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
}
