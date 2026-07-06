package com.campushub.pay.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PayCreateRequest {
    @NotBlank
    private String orderNo;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
}
