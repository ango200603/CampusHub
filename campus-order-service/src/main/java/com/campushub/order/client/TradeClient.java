package com.campushub.order.client;

import com.campushub.common.api.Result;
import com.campushub.order.vo.TradeItemVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Order Feign client.
 */
@FeignClient(name = "campus-trade-service", path = "/trades/internal/items")
public interface TradeClient {
    /**
     * Returns record details.
     */
    @GetMapping("/{id}")
    Result<TradeItemVO> detail(@PathVariable("id") Long id);

    /**
     * Locks an item for order creation.
     */
    @PostMapping("/{id}/lock-for-order")
    Result<TradeItemVO> lockForOrder(@PathVariable("id") Long id);

    /**
     * Releases a locked item.
     */
    @PostMapping("/{id}/release")
    Result<Void> release(@PathVariable("id") Long id);

    /**
     * Marks an item as sold.
     */
    @PostMapping("/{id}/sold")
    Result<Void> sold(@PathVariable("id") Long id);
}
