package com.campushub.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order entity alias mapped to orders table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("orders")
public class Order {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String orderNo;
    private Long buyerId;
    private Long sellerId;
    private Long itemId;
    private BigDecimal amount;
    private String orderType;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
