package com.campushub.trade.vo;

import com.campushub.trade.entity.TradeItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TradeItemVO {
    private Long id;
    private Long sellerId;
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    private String coverUrl;
    private String status;
    private LocalDateTime createdAt;

    public static TradeItemVO from(TradeItem item) {
        return TradeItemVO.builder()
                .id(item.getId())
                .sellerId(item.getSellerId())
                .title(item.getTitle())
                .description(item.getDescription())
                .price(item.getPrice())
                .category(item.getCategory())
                .coverUrl(item.getCoverUrl())
                .status(item.getStatus())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
