package com.campushub.ai.convert;

import com.campushub.ai.vo.AiParseResultVO;

/**
 * Static AI converters.
 */
public final class AiConvert {
    private AiConvert() {
    }

    /**
     * Converts parse result to JSON-like text for persistence.
     *
     * @param result parse result
     * @return result text
     */
    public static String toResultText(AiParseResultVO result) {
        return "{\"summary\":\"" + result.getSummary() + "\"}";
    }
}
