package com.campushub.order.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Trade item response used by order-service Feign client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeItemVO {
    private Long id;
    private Long sellerId;
    private String title;
    private BigDecimal price;
    private String status;
    private LocalDateTime createdAt;
}
