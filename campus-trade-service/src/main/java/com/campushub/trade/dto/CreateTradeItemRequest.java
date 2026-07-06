package com.campushub.trade.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateTradeItemRequest {
    @NotBlank
    @Size(max = 100)
    private String title;
    private String description;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;
    @NotBlank
    private String category;
    private String coverUrl;
}
