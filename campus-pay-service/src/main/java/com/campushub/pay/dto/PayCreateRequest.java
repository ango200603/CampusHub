package com.campushub.pay.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Existing payment creation request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayCreateRequest {
    @NotBlank
    private String orderNo;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
}
