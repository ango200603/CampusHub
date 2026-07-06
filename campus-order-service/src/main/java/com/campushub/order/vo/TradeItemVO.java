package com.campushub.order.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TradeItemVO {
    private Long id;
    private Long sellerId;
    private String title;
    private BigDecimal price;
    private String status;
    private LocalDateTime createdAt;
}
