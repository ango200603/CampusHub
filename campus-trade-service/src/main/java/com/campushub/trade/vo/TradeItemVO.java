package com.campushub.trade.vo;

import com.campushub.trade.entity.TradeItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Trade item response object.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeItemVO {
    private String id;
    private String sellerId;
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    private String coverUrl;
    private String status;
    private LocalDateTime createdAt;

    /**
     * Converts a trade item entity to VO.
     *
     * @param item trade item entity
     * @return trade item VO
     */
    public static TradeItemVO from(TradeItem item) {
        return TradeItemVO.builder()
                .id(item.getId() == null ? null : item.getId().toString())
                .sellerId(item.getSellerId() == null ? null : item.getSellerId().toString())
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
