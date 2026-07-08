package com.campushub.admin.dto;

import com.campushub.admin.enums.ReviewStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Goods review request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsReviewDTO {
    @NotNull
    private Long goodsId;
    @NotNull
    private Boolean approved;
    @NotNull
    private ReviewStatusEnum status;
    private String reason;
}
