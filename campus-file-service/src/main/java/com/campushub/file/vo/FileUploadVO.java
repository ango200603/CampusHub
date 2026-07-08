package com.campushub.file.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * File upload response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadVO {
    private Long id;
    private String originalName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String status;
    private LocalDateTime createdAt;

    /**
     * Converts a file record VO to upload VO.
     *
     * @param record file record
     * @return upload VO
     */
    public static FileUploadVO from(FileRecordVO record) {
        return FileUploadVO.builder()
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
