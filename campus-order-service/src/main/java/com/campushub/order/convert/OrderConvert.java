package com.campushub.order.convert;

import com.campushub.order.dto.CreateOrderRequest;
import com.campushub.order.dto.OrderCreateDTO;

/**
 * Static order converters.
 */
public final class OrderConvert {
    private OrderConvert() {
    }

    /**
     * Converts order create DTO to existing request.
     *
     * @param request order create DTO
     * @return create order request
     */
    public static CreateOrderRequest toCreateOrderRequest(OrderCreateDTO request) {
        return request.toCreateOrderRequest();
    }
}
