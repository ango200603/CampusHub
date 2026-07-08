package com.campushub.ai.vo;

import com.campushub.ai.entity.AiTask;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI task response object.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiTaskVO {
    private Long id;
    private Long fileId;
    private String taskType;
    private String status;
    private String resultText;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Converts an AI task entity to VO.
     *
     * @param task AI task entity
     * @return AI task VO
     */
    public static AiTaskVO from(AiTask task) {
        return AiTaskVO.builder()
                .id(task.getId())
                .fileId(task.getFileId())
                .taskType(task.getTaskType())
                .status(task.getStatus())
                .resultText(task.getResultText())
                .errorMessage(task.getErrorMessage())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
