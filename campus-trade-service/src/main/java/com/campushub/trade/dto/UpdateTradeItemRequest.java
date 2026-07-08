package com.campushub.trade.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Existing trade item update request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTradeItemRequest {
    @Size(max = 100)
    private String title;
    private String description;
    @DecimalMin("0.01")
    private BigDecimal price;
    private String category;
    private String coverUrl;
}
