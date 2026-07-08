package com.campushub.pay.dto;

import com.campushub.pay.enums.PayTypeEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payment creation request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayCreateDTO {
    @NotBlank
    private String orderNo;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @Builder.Default
    private PayTypeEnum payType = PayTypeEnum.MOCK_BALANCE;

    /**
     * Converts this DTO to the existing payment request.
     *
     * @return payment request
     */
    public PayCreateRequest toPayCreateRequest() {
        PayCreateRequest request = new PayCreateRequest();
        request.setOrderNo(orderNo);
        request.setAmount(amount);
        return request;
    }
}
