package com.campushub.ai.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI task creation request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAiTaskRequest {
    @NotNull
    private Long fileId;

    @Builder.Default
    private String taskType = "OCR_SUMMARY";
}
