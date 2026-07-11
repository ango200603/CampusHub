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
    private String id;
    private String orderNo;
    private String buyerId;
    private String sellerId;
    private String itemId;
    private String itemTitle;
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
                .id(order.getId() == null ? null : order.getId().toString())
                .orderNo(order.getOrderNo())
                .buyerId(order.getBuyerId() == null ? null : order.getBuyerId().toString())
                .sellerId(order.getSellerId() == null ? null : order.getSellerId().toString())
                .itemId(order.getItemId() == null ? null : order.getItemId().toString())
                .itemTitle(order.getItemTitle())
                .amount(order.getAmount())
                .orderType(order.getOrderType())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
