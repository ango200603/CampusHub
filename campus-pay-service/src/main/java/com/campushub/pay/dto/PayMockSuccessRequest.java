package com.campushub.pay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mock payment success request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayMockSuccessRequest {
    private String payNo;
    private String orderNo;
}
