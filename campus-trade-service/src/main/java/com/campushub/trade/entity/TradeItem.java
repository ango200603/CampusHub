package com.campushub.trade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("trade_items")
public class TradeItem {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long sellerId;
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    private String coverUrl;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
