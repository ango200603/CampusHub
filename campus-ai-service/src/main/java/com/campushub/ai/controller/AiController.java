package com.campushub.ai.controller;

import com.campushub.ai.service.AiParseService;
import com.campushub.ai.service.OcrService;
import com.campushub.ai.vo.AiParseResultVO;
import com.campushub.common.api.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lightweight AI utility endpoints.
 */
@RestController
@RequestMapping("/ai/mock")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AiController {
    private final OcrService ocrService;
    private final AiParseService aiParseService;

    /**
     * Runs a mock OCR and AI parse flow.
     *
     * @param objectKey file object key
     * @return parse result
     */
    @GetMapping("/parse")
    public Result<AiParseResultVO> parse(@RequestParam(required = false) String objectKey) {
        return Result.ok(aiParseService.parse(ocrService.extractText(objectKey)));
    }
}
