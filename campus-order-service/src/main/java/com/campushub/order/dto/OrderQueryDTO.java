package com.campushub.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order query parameters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderQueryDTO {
    private Long userId;
    private String status;
    private String orderNo;
}
