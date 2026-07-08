package com.campushub.ai.vo;

import com.campushub.ai.enums.AiTaskStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mock AI parse result.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiParseResultVO {
    private AiTaskStatusEnum status;
    private String ocrText;
    private String summary;
    private String resultText;
    private String[] keywords;
}
