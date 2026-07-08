package com.campushub.file.vo;

import com.campushub.file.entity.FileRecord;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * File record response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileRecordVO {
    private Long id;
    private String originalName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String status;
    private LocalDateTime createdAt;

    /**
     * Converts a file record entity to VO.
     *
     * @param record file record entity
     * @return file record VO
     */
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
