package com.campushub.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order creation request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDTO {
    @NotNull
    private Long itemId;
    private String orderType;

    /**
     * Converts this DTO to the existing order request.
     *
     * @return create order request
     */
    public CreateOrderRequest toCreateOrderRequest() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setItemId(itemId);
        request.setOrderType(orderType);
        return request;
    }
}
