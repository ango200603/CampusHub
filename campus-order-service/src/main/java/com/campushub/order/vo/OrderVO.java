package com.campushub.order.vo;

import com.campushub.order.entity.OrderRecord;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order response object.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    /**
     * Converts an order entity to VO.
     *
     * @param order order entity
     * @return order VO
     */
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
