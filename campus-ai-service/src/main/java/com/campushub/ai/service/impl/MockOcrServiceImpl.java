package com.campushub.ai.service.impl;

import com.campushub.ai.service.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Mock OCR implementation for local development.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class MockOcrServiceImpl implements OcrService {
    /**
     * Extracts mock text from a file object key.
     *
     * @param objectKey file object key
     * @return mock OCR text
     */
    @Override
    public String extractText(String objectKey) {
        return "模拟 OCR 文本：" + (objectKey == null ? "unknown" : objectKey);
    }
}
