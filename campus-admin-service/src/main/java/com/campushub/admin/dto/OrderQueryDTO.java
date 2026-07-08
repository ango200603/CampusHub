package com.campushub.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin order query parameters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class OrderQueryDTO {
    private String orderNo;
    private String status;
    private Long userId;
}
