package com.campushub.trade.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class UpdateTradeItemRequest {
    @Size(max = 100)
    private String title;
    private String description;
    @DecimalMin("0.01")
    private BigDecimal price;
    private String category;
    private String coverUrl;
}
