package com.campushub.trade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Goods query parameters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsQueryDTO {
    private String category;
    private String keyword;
}
