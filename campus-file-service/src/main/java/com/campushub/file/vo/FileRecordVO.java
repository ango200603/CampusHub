package com.campushub.file.vo;

import com.campushub.file.entity.FileRecord;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileRecordVO {
    private Long id;
    private String originalName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String status;
    private LocalDateTime createdAt;

    public static FileRecordVO from(FileRecord record) {
        return FileRecordVO.builder()
                .id(record.getId())
                .originalName(record.getOriginalName())
                .fileUrl(record.getFileUrl())
                .fileType(record.getFileType())
                .fileSize(record.getFileSize())
                .status(record.getStatus())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
