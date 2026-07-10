package com.campushub.trade.vo;

import com.campushub.trade.enums.GoodsStatusEnum;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Goods response object.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sellerId;
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    private String coverUrl;
    private GoodsStatusEnum status;
    private LocalDateTime createdAt;

    /**
     * Converts a trade item VO to goods VO.
     *
     * @param item trade item VO
     * @return goods VO
     */
    public static GoodsVO from(TradeItemVO item) {
        return GoodsVO.builder()
                .id(item.getId())
                .sellerId(item.getSellerId())
                .title(item.getTitle())
                .description(item.getDescription())
                .price(item.getPrice())
                .category(item.getCategory())
                .coverUrl(item.getCoverUrl())
                .status(GoodsStatusEnum.valueOf(item.getStatus()))
                .createdAt(item.getCreatedAt())
                .build();
    }
}
