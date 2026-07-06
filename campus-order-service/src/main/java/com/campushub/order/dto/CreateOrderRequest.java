package com.campushub.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {
    @NotNull
    private Long itemId;

    private String orderType = "TRADE_ITEM";
}
