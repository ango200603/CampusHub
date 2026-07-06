package com.campushub.order.vo;

import com.campushub.order.entity.OrderRecord;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long buyerId;
    private Long sellerId;
    private Long itemId;
    private BigDecimal amount;
    private String orderType;
    private String status;
    private LocalDateTime createdAt;

    public static OrderVO from(OrderRecord order) {
        return OrderVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .buyerId(order.getBuyerId())
                .sellerId(order.getSellerId())
                .itemId(order.getItemId())
                .amount(order.getAmount())
                .orderType(order.getOrderType())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
