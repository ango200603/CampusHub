package com.campushub.ai.service.impl;

import com.campushub.ai.convert.AiConvert;
import com.campushub.ai.service.AiParseService;
import com.campushub.ai.enums.AiTaskStatusEnum;
import com.campushub.ai.vo.AiParseResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Mock AI parse implementation.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class MockAiParseServiceImpl implements AiParseService {
    /**
     * Parses OCR text into a mock summary result.
     *
     * @param ocrText OCR text
     * @return parse result
     */
    @Override
    public AiParseResultVO parse(String ocrText) {
        AiParseResultVO result = AiParseResultVO.builder()
                .status(AiTaskStatusEnum.SUCCESS)
                .ocrText(ocrText)
                .summary("已完成模拟 AI 摘要")
                .keywords(new String[]{"校园", "资料", "摘要"})
                .build();
        result.setResultText(AiConvert.toResultText(result));
        return result;
    }
}
