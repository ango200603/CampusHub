package com.campushub.ai.service;

import com.campushub.ai.vo.AiParseResultVO;

/**
 * AI parse service abstraction.
 */
public interface AiParseService {
    /**
     * Parses OCR text into a summary result.
     *
     * @param ocrText OCR text
     * @return parse result
     */
    AiParseResultVO parse(String ocrText);
}
