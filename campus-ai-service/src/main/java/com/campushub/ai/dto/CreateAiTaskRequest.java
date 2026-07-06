package com.campushub.ai.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAiTaskRequest {
    @NotNull
    private Long fileId;

    private String taskType = "OCR_SUMMARY";
}
