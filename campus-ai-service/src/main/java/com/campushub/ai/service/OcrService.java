package com.campushub.ai.service;

/**
 * OCR service abstraction.
 */
public interface OcrService {
    /**
     * Extracts text from a file object key.
     *
     * @param objectKey file object key
     * @return OCR text
     */
    String extractText(String objectKey);
}
